package ru.elistratov.project.services;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ru.elistratov.project.config.BaseConnectData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.sql.*;

import static org.assertj.core.api.Assertions.assertThat;

class QueryBuilderTest {

   private static Connection dbConnection;
    private static final String TEST_DB_URL = "jdbc:mysql://localhost:3306/test_database?useSSL=false&requireSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "AndE";
    private static final String PASSWORD = "Gpt22190415";

    @BeforeAll
    public static void setUp() throws SQLException {
        dbConnection = DriverManager.getConnection(TEST_DB_URL, USER, PASSWORD);
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        dbConnection.close();
    }

    @Test
    public void testRun_FileReadAndDBWrite() throws SQLException, IOException, InterruptedException {

        File tempFile = File.createTempFile("test", ".csv");
        FileWriter writer = new FileWriter(tempFile);
        writer.write("1111,222222\n");
        writer.write("2222,333333\n");
        writer.write("3333,444444\n");
        writer.write("22,444444\n");
        writer.write("5845,44443344\n");
        writer.write("77,4756\n");
        writer.close();

        createTestTable();

        DataReadCmd dataReadCmd = new DataReadCmd(tempFile.getPath(), 0, 5);
        BaseConnectData baseConnectData = new BaseConnectData();
        baseConnectData.setUrl(TEST_DB_URL);
        baseConnectData.setUsername(USER);
        baseConnectData.setPassword(PASSWORD);
        DBConnection connection = new DBConnection(baseConnectData);
        QueryBuilder queryBuilder = new QueryBuilder(dataReadCmd, connection);

        queryBuilder.start();
        queryBuilder.join();

        assertThat(queryBuilder.isSuccess()).isTrue();
        assertThat(checkPassportByNumber("1111222222")).isTrue();
        assertThat(checkPassportByNumber("2222333333")).isTrue();
        assertThat(checkPassportByNumber("3333444444")).isTrue();
        assertThat(checkPassportByNumber("22444444")).isFalse();
        assertThat(checkPassportByNumber("584544443344")).isFalse();
        assertThat(checkPassportByNumber("774756")).isFalse();
        assertThat(tempFile.delete()).isTrue();

        deleteTablePassport();
    }

    private void createTestTable() throws SQLException {
        try (Statement statement = dbConnection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS invalid_passport (number VARCHAR(10) PRIMARY KEY)");
        }
    }

    public void deleteTablePassport() throws SQLException {
        try (Statement statement = dbConnection.createStatement()) {
            statement.execute("TRUNCATE TABLE invalid_passport");
        }
    }

    public boolean checkPassportByNumber(String passportNumber) throws SQLException {
        String query = "SELECT COUNT(*) FROM invalid_passport WHERE number = ?";
        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query)) {
            preparedStatement.setString(1, passportNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}