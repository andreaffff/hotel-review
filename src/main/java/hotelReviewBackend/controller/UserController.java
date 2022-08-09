package hotelReviewBackend.controller;

import hotelReviewBackend.JDBC.JDBC;
import hotelReviewBackend.Model.UserModel;

import org.json.JSONException;
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
        JSONObject object = null;
        Response response = null;
        //Questi due controlli li faccio qui o nei getter di password e di role ?
        String role;
        String hash = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10));
        ResultSet result = null;

        if (user.getRole() != "worker" && user.getRole() != "admin")
            role = "worker";
        else
            role = user.getRole();
        String checkUser = "SELECT * from users WHERE username=" + user.getUsername();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(checkUser);
            result = preparedStatement.executeQuery();

        } catch (Exception e) {
            e.printStackTrace();
            //throw new RuntimeException(e);
        }
        System.out.println("result:"+result);
        if (result == null) { //TODO da capire bene come funziona
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
                object = new JSONObject();
                object.put("Avviso", "Utente registrato correttamente"); //verificare se è meglio try/catch o aggiungere il metode in signature
                response = Response.status(Response.Status.OK).entity(object.toString()).build();
                //System.out.println("Utente registrato correttamente");
            } catch (SQLException e) { //TODO controllare cosa scrivere nelle parentesi
                throw new RuntimeException(e);
            } catch (JSONException e) { //sta qui per object.put
                e.printStackTrace();
            }
        } else {
            try {
                if(result.getString("username")== user.getUsername()) {
                    try {
                        object = new JSONObject();
                        object.put("Avviso", "Utente già registrato"); //verificare se è meglio try/catch o aggiungere il metode in signature
                        response = Response.status(Response.Status.CONFLICT).entity(object.toString()).build();
                        // System.out.println("Utente già registrato"); //Da inserire come risposta json
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        Response.status(400).entity(object).build();
            JDBC.closeConnection(connection);
            return response;
        }
    }
