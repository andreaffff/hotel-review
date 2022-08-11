package hotelReviewBackend.controller;

import hotelReviewBackend.JDBC.JDBC;
import hotelReviewBackend.Model.UserModel;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserController {
    public static UserModel addUser(UserModel user) {
        Connection connection = JDBC.getInstance().getConnection();
        //Questi due controlli li faccio qui o nei getter di password e di role ?
        String role;
        String hash = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10));
        ResultSet result;

        if (user.getRole() != "worker" && user.getRole() != "admin")
            role = "worker";
        else
            role = user.getRole();
        String checkUser = "SELECT * from users WHERE username = " + user.getUsername();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(checkUser);
            result = preparedStatement.executeQuery();
            while (result.next())
                System.out.println(result.getString("username"));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (result == null) {
            System.out.println("entra qui");
            String sql = "INSERT INTO users (username,name,surname,address,password,phone,role,email) VALUES (?,?,?,?,?,?,?,?)";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                System.out.println("hash=" + hash);
                 System.out.println("role=" + role);
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getName());
                preparedStatement.setString(3, user.getSurname());
                preparedStatement.setString(4, user.getAddress());
                preparedStatement.setString(5, hash);
                preparedStatement.setString(6, user.getPhone());
                preparedStatement.setString(7, role);
                preparedStatement.setString(8, user.getEmail());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
       }else
           System.out.println("Utente già registrato"); //Da inserire come risposta json

        JDBC.closeConnection(connection);
        return user;
    }

    public static UserModel getUserByUsername(String username){
        Connection connection = JDBC.getInstance().getConnection();
        UserModel user = new UserModel();

        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println(username);

            while (resultSet.next()) {
                user.setUsername(resultSet.getString("username"));
                user.setName(resultSet.getString("name"));
                user.setSurname(resultSet.getString("surname"));
                user.setEmail(resultSet.getString("email"));
                user.setPhone(resultSet.getString("phone"));
                user.setAddress(resultSet.getString("address"));
                user.setPassword(resultSet.getString("password"));
                user.setRole(resultSet.getString("role"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        JDBC.closeConnection(connection);
        return user;
    }
}
