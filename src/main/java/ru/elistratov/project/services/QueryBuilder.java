package ru.elistratov.project.services;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;

@Slf4j
public class QueryBuilder extends Thread {

    private final DataReadCmd dataReadCmd;
    private final DBConnection connection;
    private StringBuilder builder = new StringBuilder();

    private boolean isSuccess = false;

    public QueryBuilder(DataReadCmd dataReadCmd, DBConnection connection) {
        this.dataReadCmd = dataReadCmd;
        this.connection = connection;
    }
    public boolean isSuccess() {
        return isSuccess;
    }

    @Override
    public void run() {
        int count = dataReadCmd.getBeginLine();
        int endLine = dataReadCmd.getEndLine();
        String[] lineInArray;
        try {
            CSVReader csvReader = new CSVReaderBuilder(new FileReader(dataReadCmd.getFilePath()))
                    .withSkipLines(dataReadCmd.getBeginLine())
                    .build();
            while ((lineInArray = csvReader.readNext()) != null && count <= endLine) {
                buildQuery(lineInArray);
                count++;
            }
            connection.executeMultiInsertPassport(builder);
            csvReader.close();
            isSuccess = true;
        } catch (Exception e) {
            log.error(Thread.currentThread().getName() + ": error: " + e.getMessage());
        }
    }
    private void buildQuery(String[] lineInArray) throws Exception {
        if (lineInArray.length != 2
                || lineInArray[0].length() != 4
                || lineInArray[1].length() != 6) {
            return;
        }
        builder.append(builder.length() == 0 ? "('" : ", ('");
        builder.append(lineInArray[0]).append(lineInArray[1]).append("')");
        if (builder.length() > 100_000) {
            connection.executeMultiInsertPassport(builder);
            builder = new StringBuilder();
        }
    }

}
