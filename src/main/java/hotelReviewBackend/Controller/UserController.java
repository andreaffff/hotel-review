package hotelReviewBackend.Controller;

import hotelReviewBackend.JDBC.JDBC;
import hotelReviewBackend.Model.UserModel;
import hotelReviewBackend.Model.LoginModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserController {
    static Response response = null;
    static JSONObject object;
    static ResultSet result = null;
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
    public static List<UserModel> getAllUsers(){
        Connection connection = JDBC.getInstance().getConnection();
       List<UserModel> Users =  new ArrayList<>();

        String sql = "SELECT * FROM users";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                UserModel User = new UserModel();
                User.setUsername(resultSet.getString("username"));
                User.setName(resultSet.getString("name"));
                User.setSurname(resultSet.getString("surname"));
                User.setEmail(resultSet.getString("email"));
                User.setPhone(resultSet.getString("phone"));
                User.setAddress(resultSet.getString("address"));
                User.setPassword(resultSet.getString("password"));
                User.setRole(resultSet.getString("role"));
                Users.add(User);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        JDBC.closeConnection(connection);
        return Users;
    }

    //Delete user
    public static Response deleteUser(String username) {
        Connection connection = JDBC.getInstance().getConnection();

        String sql = "DELETE  FROM users WHERE username = ? ";
        String getOneSql = "SELECT username FROM users WHERE username = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getOneSql);
            preparedStatement.setString(1, username);
            result = preparedStatement.executeQuery();

        }catch(SQLException e){
            e.printStackTrace();
        }

        try {
            if(result.next()) {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, username);
                preparedStatement.executeUpdate();
                object = new JSONObject();
                object.put("Avviso", "Utente eliminato correttamente"); // admin only
                response = Response.status(Response.Status.OK).entity(object.toString()).build();
            }else{
                object = new JSONObject();
                try {
                    object.put("Avviso", "Errore durante l'eliminazione, utente non riconosciuto");
                }catch(JSONException e){
                    e.printStackTrace();
                }
                response = Response.status(Response.Status.BAD_REQUEST).entity(object.toString()).build();
            }

        } catch (Exception e) {
           // throw new RuntimeException(e);
            e.printStackTrace();
        }

            JDBC.closeConnection(connection);
        return response ;
    }

    public static Response updateUser(UserModel user, String username) {
        Connection connection = JDBC.getInstance().getConnection();
        String updateUserSql = "UPDATE users SET username = ?, name = ?, surname = ?, address = ?, password = ?, phone = ?, email = ? WHERE username = ?";
        String getOneSql = "SELECT username FROM users WHERE username = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getOneSql);
            preparedStatement.setString(1, username);
            result = preparedStatement.executeQuery();

        }catch(SQLException e){
            e.printStackTrace();
        }

        try {
            String hash = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10));
            if(result.next()) {
                PreparedStatement preparedStatement = connection.prepareStatement(updateUserSql);
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getName());
                preparedStatement.setString(3, user.getSurname());
                preparedStatement.setString(4, user.getAddress());
                preparedStatement.setString(5, hash);
                preparedStatement.setString(6, user.getPhone());
                preparedStatement.setString(7, user.getEmail());
                preparedStatement.setString(8, username);
                preparedStatement.executeUpdate();

                object = new JSONObject();
                object.put("Avviso", "Utente modificato correttamente"); // admin only
                response = Response.status(Response.Status.OK).entity(object.toString()).build();
            }else{
                object = new JSONObject();
                try {
                    object.put("Avviso", "Errore durante l'aggiornamento, utente non riconosciuto");
                }catch(JSONException e){
                    e.printStackTrace();
                }
                response = Response.status(Response.Status.BAD_REQUEST).entity(object.toString()).build();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        JDBC.closeConnection(connection);
        return response;
    }
}