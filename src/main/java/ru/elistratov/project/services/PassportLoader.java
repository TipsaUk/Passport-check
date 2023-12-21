package ru.elistratov.project.services;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.elistratov.project.model.ListVersion;
import ru.elistratov.project.model.StatusCode;
import ru.elistratov.project.repositories.ListVersionRepository;
import ru.elistratov.project.repositories.PassportRepository;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PassportLoader {
    private final DBConnection connection;
    @Value("${load.urlToFilePassports}")
    private String urlToFilePassports;
    private final PassportRepository passportRepository;
    private final ListVersionRepository listVersionRepository;

    @Scheduled(cron = "${load.schedule}")
    public void updateInvalidPassports() {
        log.info("Start time of loading a file: " + LocalDateTime.now());
        File filePassports = downloadNewFilePassports();
        if (filePassports == null) {
            return;
        }
        savePassportsToSQL(filePassports);
        if (filePassports.delete()) {
            log.info("The file was deleted after loading");
        }
    }
    private File downloadNewFilePassports() {
        try {
            File tempFile = File.createTempFile("passports", ".csv");
            FileUtils.copyURLToFile(new URL(urlToFilePassports), tempFile);
            return tempFile;
        } catch (IOException e) {
            log.error("Error when loading a file: " + e.getMessage());
            return null;
        }
    }

    private void savePassportsToSQL(File filePassports) {
        saveListVersion(StatusCode.LOAD, "");
        try {
            connection.deleteTablePassport();
            int countSuccess = 0;
            int countThread = Runtime.getRuntime().availableProcessors();
            int lineCountTotal = getTotalLinesApproximately(filePassports);
            int lineCountOnThread = lineCountTotal / countThread;
            QueryBuilder[] threads = new QueryBuilder[countThread];
            for (int i = 0; i < countThread; i++) {
                threads[i] = new QueryBuilder(new DataReadCmd(filePassports.getPath(), lineCountOnThread * i
                     , i == countThread - 1 ? lineCountTotal : lineCountOnThread * i + lineCountOnThread - 1)
                     , connection);
                threads[i].start();
            }
            for (QueryBuilder thread : threads) {
                thread.join();
                if (thread.isSuccess()) {
                    countSuccess++;
                }
            }
            saveListVersion(countThread == countSuccess ? StatusCode.SUCCESS : StatusCode.ERROR, "");
        } catch (Exception e) {
            saveListVersion(StatusCode.ERROR, e.getMessage());
        }
    }

    private int getTotalLinesApproximately(File filePassports) {
        int charsPerLine = 12;
        long fileSizeInBytes = filePassports.length();
        return (int) fileSizeInBytes / charsPerLine;
    }

    private void saveListVersion(StatusCode statusCode, String error) {
        ListVersion listVersion = listVersionRepository
                .findListVersionByUploadDate(LocalDate.from(LocalDateTime.now())).orElse(new ListVersion());
        listVersion.setUploadDate(LocalDate.from(LocalDateTime.now()));
        listVersion.setStatusCode(statusCode);
        listVersion.setError(error);
        listVersion.setAmountPassports(passportRepository.count());
        listVersionRepository.save(listVersion);
        writeLog(statusCode, error);
    }

    private void writeLog(StatusCode statusCode, String error) {
        switch (statusCode) {
            case LOAD ->
                    log.info("Start time of recording a new list: " + LocalDateTime.now());
            case SUCCESS ->
                    log.info("The end time of recording the list of passports: " + LocalDateTime.now());
            case ERROR ->
                    log.error("Error recording the list of passports: " + LocalDateTime.now() + " error: " + error);
        }
    }

}
