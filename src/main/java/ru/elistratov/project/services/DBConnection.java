package ru.elistratov.project.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.elistratov.project.config.BaseConnectData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Repository
@RequiredArgsConstructor
public class DBConnection {

    private Connection connection;
    private final BaseConnectData baseConnectData;

    public Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(
                        baseConnectData.getUrl() +
                                "&user=" + baseConnectData.getUsername() + "&password=" + baseConnectData.getPassword());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public void executeMultiInsertPassport(StringBuilder builder) throws SQLException {
        if (builder.length() == 0) {
            return;
        }
        String sql = "INSERT IGNORE INTO invalid_passport(number) VALUES " + builder;
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    public void deleteTablePassport() throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("TRUNCATE TABLE invalid_passport");
        }
    }

}
