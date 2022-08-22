package hotelReviewBackend.Controller;

import hotelReviewBackend.JDBC.JDBC;
import hotelReviewBackend.Model.UserModel;
import hotelReviewBackend.Model.LoginModel;

import org.json.JSONObject;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserController {
    public static Response addUser(UserModel user) {
        Connection connection = JDBC.getInstance().getConnection();
        JSONObject object ;
        Response response = null;
        ResultSet result;
        String checkUser = "SELECT * from users WHERE username= ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(checkUser);
            preparedStatement.setString(1, user.getUsername());
            result = preparedStatement.executeQuery();
            String hash = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10));

            if (!result.next()) {  //Se la ricerca non ha prodotto risultati
                //Query inserimento utenti DB
                String sql = "INSERT INTO users (username,name,surname,address,password ,phone,role,email) VALUES (?,?,?,?,?,?,?,?)";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getName());
                preparedStatement.setString(3, user.getSurname());
                preparedStatement.setString(4, user.getAddress());
                preparedStatement.setString(5, hash);
                preparedStatement.setString(6, user.getPhone());
                preparedStatement.setString(7, user.getRole());
                preparedStatement.setString(8, user.getEmail());
                System.out.println("CIAOaaaa");
                preparedStatement.executeUpdate(); //aggiungi il nuovo utente nel DB
                object = new JSONObject();
                object.put("Avviso", "Utente registrato correttamente"); //verificare se è meglio try/catch o aggiungere il metode in signature
                response = Response.status(Response.Status.OK).entity(object.toString()).build();

            } else if (result.getString("username").equals(user.getUsername())) {
                object = new JSONObject();
                object.put("Avviso", "Utente già registrato"); //verificare se è meglio try/catch o aggiungere il metodo in signature e creare una classe che gestisce le eccezioni
                response = Response.status(Response.Status.CONFLICT).entity(object.toString()).build();
                Response.status(400).
                        entity(object).
                        build();
            }

        }catch(Exception e ){ //Dovrebbe prendere tutte le eccezioni
            e.printStackTrace();
        }
        JDBC.closeConnection(connection);
        return response;
    }
    public static Response login(LoginModel user){
        JSONObject object ;
        Response response = null;
        ResultSet result;
        //Cerco la password tramite lo username
        String checkUser = "SELECT password from users WHERE username= ?";
        try{
            Connection connection = JDBC.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(checkUser);
            preparedStatement.setString(1, user.getUsername());
            result = preparedStatement.executeQuery();

            if(result.next()) { //Se lo username è nel DB
                if (BCrypt.checkpw(user.getPassword(), result.getString("password"))) {
                    object = new JSONObject();
                    object.put("Avviso", "Login riuscito");
                    response = Response.status(Response.Status.OK).entity(object.toString()).build();
                } else {
                    object = new JSONObject();
                    object.put("Avviso", "Password errata");
                    response = Response.status(Response.Status.UNAUTHORIZED).entity(object.toString()).build();
                }
            }else{ //Se l'utente non è nel DB
                object = new JSONObject();
                object.put("Avviso", "Username errato o non ancora registrato");
                response = Response.status(Response.Status.NOT_FOUND).entity(object.toString()).build();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return response;
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
                System.out.println(user.getPassword());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        JDBC.closeConnection(connection);
        return user;
    }
    //Credo che questa operazione la possano fare solo gli admin
    public static UserModel getUser(){
        Connection connection = JDBC.getInstance().getConnection();
        UserModel User = new UserModel();

        String sql = "SELECT * FROM users";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                User.setUsername(resultSet.getString("username"));
                User.setName(resultSet.getString("name"));
                User.setSurname(resultSet.getString("surname"));
                User.setEmail(resultSet.getString("email"));
                User.setPhone(resultSet.getString("phone"));
                User.setAddress(resultSet.getString("address"));
                User.setPassword(resultSet.getString("password"));
                User.setRole(resultSet.getString("role"));

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        JDBC.closeConnection(connection);
        return User;
    }

    //Delete user
    public static int deleteUser(String username) {
        Connection connection = JDBC.getConnection();
        int result;

        String sql = "DELETE FROM users WHERE username = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            result = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

            JDBC.closeConnection(connection);
        return result;
    }

    public static UserModel updateUser(UserModel user) {
        Connection connection = JDBC.getConnection();
        UserModel userResult = new UserModel();

        String sql = "UPDATE users SET usernane = ?, name = ?, surname = ?, address = ?, password = ?, phone = ?, email = ? WHERE username = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getName());
            preparedStatement.setString(3, user.getSurname());
            preparedStatement.setString(4, user.getAddress());
            preparedStatement.setString(5, user.getPassword());
            preparedStatement.setString(6, user.getPhone());
            preparedStatement.setString(7, user.getRole());
            preparedStatement.setString(8, user.getEmail());
            preparedStatement.executeUpdate();

            userResult.setUsername(user.getUsername());
            userResult.setName(user.getName());
            userResult.setSurname(user.getSurname());
            userResult.setAddress(user.getAddress());
            userResult.setPassword(user.getPassword());
            userResult.setPhone(user.getPhone());
            userResult.setEmail(user.getEmail());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        JDBC.closeConnection(connection);
        return userResult;
    }
}