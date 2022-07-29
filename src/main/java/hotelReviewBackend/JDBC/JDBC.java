package hotelReviewBackend.JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBC {
    private static JDBC instance = null;
    private final String url;
    private final String user;
    private final String password;

    private JDBC() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            this.url = "jdbc:mysql://localhost/hoteladvisor";
            this.user = "root";
            this.password = "alberello00";

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
            connection = DriverManager.getConnection(url, user, password);
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