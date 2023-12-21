package ru.elistratov.project.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.elistratov.project.dto.CheckDTO;
import ru.elistratov.project.dto.CheckPassportDto;
import ru.elistratov.project.dto.CheckServiceDto;
import ru.elistratov.project.dto.PassportDto;
import ru.elistratov.project.model.InvalidPassport;
import ru.elistratov.project.model.ListVersion;
import ru.elistratov.project.model.StatusCode;
import ru.elistratov.project.repositories.ListVersionRepository;
import ru.elistratov.project.repositories.PassportRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PassportService {

    private final PassportRepository passportRepository;
    private final ListVersionRepository listVersionRepository;

    public CheckDTO checkPassport(PassportDto passportDto) throws EntityNotFoundException {
        log.info("checkPassport: " + passportDto + " (Start method)");
        LocalDate versionDate = getVersionDate();
        ArrayList<CheckPassportDto> passportList = new ArrayList<>();
        String fullNumber = passportDto.getSeries() + passportDto.getNumber();
        if (passportDto.getNumber() == null
                || passportDto.getSeries() == null || fullNumber.length() > 10) {
            return new CheckDTO(false, versionDate,"Format passport is wrong", passportList);
        }
        passportList.add(new CheckPassportDto(passportRepository.findByNumber(fullNumber)
                .isPresent(), passportDto.getSeries(), passportDto.getNumber()));
        return new CheckDTO(true, versionDate, null, passportList);
    }

    public CheckDTO checkPassports(List<PassportDto> passports) throws EntityNotFoundException {
        log.info("checkPassports: " + passports + " (Start method)");
        LocalDate versionDate = getVersionDate();
        return new CheckDTO(true, versionDate, null
                , passportRepository.findAllByNumberIn(passports.stream().map(p
                -> p.getSeries() + p.getNumber()).toList()).stream().map(i
                -> new CheckPassportDto(true, separateNumbers(i)[0], separateNumbers(i)[1])).toList());
    }

    public CheckServiceDto checkService() {
        log.info("checkService: (Start method)");
        ListVersion listVersion = listVersionRepository.findTopByOrderByUploadDateDesc();
        if (listVersion == null) {
            return new CheckServiceDto(false, null);
        }
        return new CheckServiceDto(listVersion.getStatusCode() == StatusCode.SUCCESS, listVersion.getUploadDate());
    }

    private LocalDate getVersionDate() throws EntityNotFoundException {
        ListVersion listVersion = listVersionRepository.findTopByOrderByUploadDateDesc();
        if (listVersion == null || listVersion.getStatusCode() != StatusCode.SUCCESS) {
            throw new EntityNotFoundException("The list of passports is not available");
        }
        return listVersion.getUploadDate();
    }

    private String[] separateNumbers(InvalidPassport invalidPassport) {
        int seriesLength = 4;
        int fullLength = 10;
        String number = invalidPassport.getNumber();
        return new String[]{number.substring(0, Math.min(number.length(), seriesLength))
                , number.substring(seriesLength, Math.min(number.length(), fullLength + 1))};
    }
}
