package hotelReviewBackend.Controller;

import hotelReviewBackend.JDBC.JDBC;
import hotelReviewBackend.Model.LoginModel;
import hotelReviewBackend.Model.UserModel;
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
//TODO update role

    public static Response addUser(UserModel user) {
        Connection connection = JDBC.getInstance().getConnection();
        JSONObject object;
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

        } catch (Exception e) { //Dovrebbe prendere tutte le eccezioni
            e.printStackTrace();
        }
        JDBC.closeConnection(connection);
        return response;
    }

    public static Response login(LoginModel user) {
        JSONObject object;
        Response response = null;
        ResultSet result;
        //Cerco la password tramite lo username
        String checkUser = "SELECT password from users WHERE username= ?";
        try {
            Connection connection = JDBC.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(checkUser);
            preparedStatement.setString(1, user.getUsername());
            result = preparedStatement.executeQuery();

            if (result.next()) { //Se lo username è nel DB
                if (BCrypt.checkpw(user.getPassword(), result.getString("password"))) {
                    object = new JSONObject();
                    object.put("Avviso", "Login riuscito");
                    response = Response.status(Response.Status.OK).entity(object.toString()).build();
                } else {
                    object = new JSONObject();
                    object.put("Avviso", "Password errata");
                    response = Response.status(Response.Status.UNAUTHORIZED).entity(object.toString()).build();
                }
            } else { //Se l'utente non è nel DB
                object = new JSONObject();
                object.put("Avviso", "Username errato o non ancora registrato");
                response = Response.status(Response.Status.NOT_FOUND).entity(object.toString()).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public static UserModel getUserByUsername(String username) {
        Connection connection = JDBC.getInstance().getConnection();
        UserModel user = new UserModel();

        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            result = preparedStatement.executeQuery();
            if (result.next()) {
                user.setUsername(result.getString("username"));
                user.setName(result.getString("name"));
                user.setSurname(result.getString("surname"));
                user.setEmail(result.getString("email"));
                user.setPhone(result.getString("phone"));
                user.setAddress(result.getString("address"));
                user.setPassword(result.getString("password"));
                user.setRole(result.getString("role"));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        JDBC.closeConnection(connection);
        return user;
    }

    //Credo che questa operazione la possano fare solo gli admin
    public static Response getAllUsers(String username) {
        Connection connection = JDBC.getInstance().getConnection();
        int condition = 0;
        condition = checkAdmin(username,connection);
        try {
            if (condition == 0) {
                object = new JSONObject();
                object.put("Avviso", "Si è verificato un errore con il tuo account");
                response = Response.status(Response.Status.UNAUTHORIZED).entity(object.toString()).build();

            } else if (condition == 1) {
                object = new JSONObject();
                object.put("Avviso", "Non hai il permesso per questa operazione");
                //TODO riportare alla pagina di login
                response = Response.status(Response.Status.NOT_FOUND).entity(object.toString()).build();
                //TODO TODO TODO non si possono vedere i messaggi di errore perchè il return è l'array
            } else if (condition == 2) {
                String sql = "SELECT * FROM users";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();
                object = new JSONObject();

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

                    object.accumulate("All users",User.toJson());
                    response = Response.status(Response.Status.NOT_FOUND).entity(object.toString()).build();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JDBC.closeConnection(connection);


        return response;
    } //FATTO

    //Delete user
    public static Response deleteUser(String username, UserModel userToDelete) {
        Connection connection = JDBC.getInstance().getConnection();
        int condition = checkUsernameMatchAndIsAdmin(userToDelete,username,connection);

        try {
            // Se l'utente che vuole fare l'eliminazione non è admin o non sta eliminando il proprio account
            if (condition == 0) {
                //TODO in caso l'utente elimina se stesso fare logout
                object = new JSONObject();
                object.put("Avviso", "Si è verificato un errore con il tuo account");
                response = Response.status(Response.Status.NOT_FOUND).entity(object.toString()).build();

                //Se l'utente che vuole fare l'eliminazione non è nel DB
            } else if (condition == 1) {
                object = new JSONObject();
                object.put("Avviso", "Non hai il permesso per questa operazione");
                //TODO riportare alla pagina di login
                response = Response.status(Response.Status.UNAUTHORIZED).entity(object.toString()).build();

            } else if (condition == 2) {
                String sql = "DELETE  FROM users WHERE username = ? ";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, userToDelete.getUsername());
                preparedStatement.executeUpdate();
                object = new JSONObject();
                object.put("Avviso", "Utente eliminato correttamente"); // admin only
                response = Response.status(Response.Status.OK).entity(object.toString()).build();
            }

        } catch (Exception e) {
            // throw new RuntimeException(e);
            e.printStackTrace();
        }

        JDBC.closeConnection(connection);
        return response;
    } //FATTO
//TODO TODO problema di username da cambiare, nuovo username e username di chi vuole fare la modi
    public static Response updateUser(String username, UserModel user) {
        Connection connection = JDBC.getInstance().getConnection();
        String updateUserSql = "UPDATE users SET username = ?, name = ?, surname = ?, address = ?, password = ?, phone = ?, email = ? WHERE username = ?";
        String getOneSql = "SELECT username FROM users WHERE username = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getOneSql);
            preparedStatement.setString(1, user.getUsername());
            result = preparedStatement.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            String hash = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10));
            if (result.next()) {
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
            } else {
                object = new JSONObject();
                try {
                    object.put("Avviso", "Errore durante l'aggiornamento, utente non riconosciuto");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                response = Response.status(Response.Status.BAD_REQUEST).entity(object.toString()).build();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        JDBC.closeConnection(connection);
        return response;
    } //FATTO


    public static int checkAdmin(String username, Connection connection) {
        System.out.println("Entra qui");
        String getOneSql = "SELECT * FROM users WHERE username = ?";
        int condition = 0; //0 se non c'è l'utente che esegue l'eliminazione nel DB
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getOneSql);
            preparedStatement.setString(1, username);
            result = preparedStatement.executeQuery();
            if (result.next()) {
                condition = 1; //1 se c'è l'utente che esegue l'eliminazione ma non si hanno i permessi
                if (result.getString("role").equals("admin"))
                    condition = 2; // se l'utente che vuole fare l'eliminazione è presente e ha i permessi
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return condition;
    }

    public static int checkUsernameMatchAndIsAdmin(UserModel userToCheck, String username, Connection connection) {
        String getOneSql = "SELECT * FROM users WHERE username = ?";
        int condition = 0; //0 se non c'è l'utente che esegue l'eliminazione nel DB
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getOneSql);
            preparedStatement.setString(1, username);
            result = preparedStatement.executeQuery();
            if (result.next()) {
                condition = 1; //1 se c'è l'utente che esegue l'eliminazione ma non si hanno i permessi

                if (result.getString("username").equals(userToCheck.getUsername()) || result.getString("role").equals("admin"))
                    condition = 2; // se l'utente che vuole fare l'eliminazione è presente e ha i permessi
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return condition;
    }
}