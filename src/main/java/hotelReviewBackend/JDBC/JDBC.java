package hotelReviewBackend.JDBC;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBC {
    static Dotenv dotenv = Dotenv.configure().load();
    private static final String URL = dotenv.get("URL");//"jdbc:mysql://localhost/hoteladvisor";;
    private static final String USER = dotenv.get("USER");//"root";
    private static final String PASSWORD = dotenv.get("PASSWORD");//"wPa06@3oeCq3" ;
    private static final String DRIVER = dotenv.get("DRIVER");//"com.mysql.cj.jdbc.Driver";
    private static JDBC instance = null;

    private JDBC() {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static JDBC getInstance() {
        if (instance == null)
            instance = new JDBC();

        return instance;
    }

    public static void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
}