package hotelReviewBackend.controller;

import hotelReviewBackend.JDBC.JDBC;
import hotelReviewBackend.Model.UserModel;

import org.json.JSONObject;

import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

            if (!result.next() && result != null) {  //Se la ricerca non ha prodotto risultati
                System.out.println("entra qui if if ");
                String sql = "INSERT INTO users (username,name,surname,address,password,phone,role,email) VALUES (?,?,?,?,?,?,?,?)";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getName());
                preparedStatement.setString(3, user.getSurname());
                preparedStatement.setString(4, user.getAddress());
                preparedStatement.setString(5, user.getEncryptedPassword());
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
        }