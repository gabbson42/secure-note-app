package project.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private final static String URL = "jdbc:mysql://localhost:3306/secure_notes";
    private final static String USER = "root";
    private final static String PASSWORD = "gabbe123";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

}
