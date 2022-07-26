package hotelReviewBackend.JDBC;

import java.sql.*;

public class JDBC {
    private static JDBC instance = null;
    private final String URL;
    private final String USER;
    private final String PASSWORD;
    private final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private JDBC() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            this.URL = "jdbc:mysql://localhost:3306/hoteladvisor";
            this.USER = "root";
            this.PASSWORD = "wPa06@3oeCq3";

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static JDBC getInstance() {
        if (instance == null)
            instance = new JDBC();

        return instance;
    }

    public Connection getConnection() {
        Connection connection;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    public static void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}