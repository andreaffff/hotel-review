package hotelReviewBackend.Controller;

import hotelReviewBackend.JDBC.JDBC;
import hotelReviewBackend.Model.LoginModel;
import hotelReviewBackend.Model.UpdatePasswordOrUsernameModel;
import hotelReviewBackend.Model.UserModel;
import org.json.JSONObject;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserController {
    static Response response = null;
    static JSONObject object;
    static ResultSet result = null;

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

    public static Response getAllUsers(String username) {
        Connection connection = JDBC.getInstance().getConnection();
        int condition = 0;
        condition = checkAdmin(username,connection);
        try {
            if (condition == 0) {  //Se l'utente che vuole fare la get non è nel DB
                object = new JSONObject();
                object.put("Avviso", "Si è verificato un errore con il tuo account");
                response = Response.status(Response.Status.UNAUTHORIZED).entity(object.toString()).build();

            } else if (condition == 1) { //Se l'utente che vuole fare la get non ha i permessi
                object.put("Avviso", "Non hai il permesso per questa operazione");
                //TODO riportare alla pagina di login
                response = Response.status(Response.Status.NOT_FOUND).entity(object.toString()).build();
            } else if (condition == 2) {
                String sql = "SELECT * FROM users";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();
                object = new JSONObject();

                while (resultSet.next()) {
                    UserModel user = new UserModel();
                    user.setUsername(resultSet.getString("username"));
                    user.setName(resultSet.getString("name"));
                    user.setSurname(resultSet.getString("surname"));
                    user.setEmail(resultSet.getString("email"));
                    user.setPhone(resultSet.getString("phone"));
                    user.setAddress(resultSet.getString("address"));
                    user.setPassword(resultSet.getString("password"));
                    user.setRole(resultSet.getString("role"));

                    object.accumulate("All users",user.toJson());
                    response = Response.status(Response.Status.OK).entity(object.toString()).build();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JDBC.closeConnection(connection);


        return response;
    } //admin only
    //Delete user
    public static Response deleteUser(String username, UserModel userToDelete) {
        Connection connection = JDBC.getInstance().getConnection();
        try {
            String getOneSql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(getOneSql);
            preparedStatement.setString(1, userToDelete.getUsername());
            result = preparedStatement.executeQuery();

            if (result.next()) {
                String sql = "DELETE  FROM users WHERE username = ? ";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, userToDelete.getUsername());
                preparedStatement.executeUpdate();
                object = new JSONObject();
                object.put("Avviso", "Utente eliminato correttamente"); // admin only
                response = Response.status(Response.Status.OK).entity(object.toString()).build();
            } else {
                object = new JSONObject();
                object.put("Avviso", "Utente da eliminare non trovato"); // admin only
                response = Response.status(Response.Status.NOT_FOUND).entity(object.toString()).build();
            }


        } catch (Exception e) {
            // throw new RuntimeException(e);
            e.printStackTrace();
        }

        JDBC.closeConnection(connection);
        return response;
    } //admin only or username

    public static Response updateUser(String username, UserModel user) {
        Connection connection = JDBC.getInstance().getConnection();
        String updateUserSql = "UPDATE users SET name = ?, surname = ?, address = ?, phone = ?, email = ? WHERE username = ?";
        String getOneSql = "SELECT username FROM users WHERE username = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getOneSql);
            preparedStatement.setString(1, username);
            result = preparedStatement.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (result.next()) { // Se chi vuole fare l'aggiornamento è nel DB
                PreparedStatement preparedStatement = connection.prepareStatement(updateUserSql);
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getSurname());
                preparedStatement.setString(3, user.getAddress());
                preparedStatement.setString(4, user.getPhone());
                preparedStatement.setString(5, user.getEmail());
                preparedStatement.setString(6, username);
                preparedStatement.executeUpdate();

                object = new JSONObject();
                object.put("Avviso", "Utente modificato correttamente"); // admin only
                response = Response.status(Response.Status.OK).entity(object.toString()).build();
            } else {
                object = new JSONObject();
                object.put("Avviso", "Errore durante l'aggiornamento, utente non riconosciuto");
                response = Response.status(Response.Status.BAD_REQUEST).entity(object.toString()).build();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        JDBC.closeConnection(connection);
        return response;
    } //Modificabile solo ne "il tuo profilo"

    public static Response updateRole(String username, UserModel user){
        Connection connection = JDBC.getInstance().getConnection();
        String updateRoleSql = "UPDATE users SET role = ? WHERE username = ?";
        int condition = checkAdmin(username,connection);
        try{
            if(condition == 0){ //Se l'utente che vuole fare l'aggiornamento non è nel DB
                object = new JSONObject();
                object.put("Avviso", "Si è verificato un errore con il tuo account");
                response = Response.status(Response.Status.NOT_FOUND).entity(object.toString()).build();

            }else if (condition == 1){ // Se l'utente che vuole fare l'aggiornamento non è admin o non sta eliminando il proprio account
                object = new JSONObject();
                object.put("Avviso", "Non hai il permesso per questa operazione");
                response = Response.status(Response.Status.UNAUTHORIZED).entity(object.toString()).build();

            }else if (condition == 2) {
                PreparedStatement preparedStatement = connection.prepareStatement(updateRoleSql);
                preparedStatement.setString(1, user.getRole());
                preparedStatement.setString(2, user.getUsername());

                int rowsAffected = preparedStatement.executeUpdate();

                if(rowsAffected > 0) {
                    object = new JSONObject();
                    object.put("Avviso", "Ruolo cambiato con successo");
                    response = Response.status(Response.Status.OK).entity(object.toString()).build();
                }
                else {
                    object = new JSONObject();
                    object.put("Avviso", "Si è verificato un errore durante l'operazione");
                    response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(object.toString()).build();
                }
            }
            } catch (Exception e) {
                e.printStackTrace();
            }

        return response;
    } //admin only

    public static Response updatePassword(String username, UpdatePasswordOrUsernameModel password) {

        Connection connection = JDBC.getInstance().getConnection();
        String checkUser = "SELECT password from users WHERE username = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(checkUser);
            preparedStatement.setString(1, username);
            result = preparedStatement.executeQuery();

            if (result.next()) { //Se lo username è nel DB
                //controllo password vecchia in chiaro con la criptata del DB
                if (BCrypt.checkpw(password.getOldValue(), result.getString("password"))) {
                    String hash = BCrypt.hashpw(password.getNewValue(), BCrypt.gensalt(10));
                    String updatePasswordSQL = "UPDATE users SET password = ? WHERE username = ?";
                    preparedStatement = connection.prepareStatement(updatePasswordSQL);
                    preparedStatement.setString(1, hash);
                    preparedStatement.setString(2, username);
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected>0){
                        object = new JSONObject();
                        object.put("Avviso","Password cambiata con successo");
                        response = Response.status(Response.Status.OK).entity(object.toString()).build();
                    }else{
                        object = new JSONObject();
                        object.put("Avviso","Si è verificato un errore");
                        response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(object.toString()).build();
                    }
                } else {
                    object = new JSONObject();
                    object.put("Avviso", "Password errata");
                    response = Response.status(Response.Status.UNAUTHORIZED).entity(object.toString()).build();
                }
            }else {
                object = new JSONObject();
                object.put("Avviso", "Si è verificato un errore con il tuo account");
                response = Response.status(Response.Status.NOT_FOUND).entity(object.toString()).build();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    } //username only

    public static Response updateUsername(String username, UpdatePasswordOrUsernameModel updateUsername ){
        Connection connection = JDBC.getInstance().getConnection();
        String getOneSql = "SELECT username FROM users WHERE username = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getOneSql);
            preparedStatement.setString(1, username);
            result = preparedStatement.executeQuery();

            if(result.next()){ //Se l'utente è nel DB
                if(username.equals(updateUsername.getOldValue())){ //Se chi modifica equivale a chi viene modificato
                    //Query per vedere se il nuovo username è già in uso
                    preparedStatement = connection.prepareStatement(getOneSql);
                    preparedStatement.setString(1, updateUsername.getNewValue());
                    result = preparedStatement.executeQuery();

                    if(!result.next()){ // Se nessuno ha quello username posso aggiornarlo

                        String updateUsernameSQL = "UPDATE users SET username = ? WHERE username = ?";
                        preparedStatement = connection.prepareStatement(updateUsernameSQL);
                        preparedStatement.setString(1, updateUsername.getNewValue());
                        preparedStatement.setString(2, updateUsername.getOldValue());
                        int rowsAffected = preparedStatement.executeUpdate();

                        if (rowsAffected>0){
                            object = new JSONObject();
                            object.put("Avviso","Username cambiato con successo");
                            response = Response.status(Response.Status.OK).entity(object.toString()).build();
                        }else{
                            object = new JSONObject();
                            object.put("Avviso","Si è verificato un errore");
                            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(object.toString()).build();
                        }
                    } else {
                        object = new JSONObject();
                        object.put("Avviso", "Questo username è già in uso");
                        response = Response.status(Response.Status.CONFLICT).entity(object.toString()).build();
                    }

                }else{
                    object = new JSONObject();
                    object.put("Avviso", "Non hai il permesso per modificare lo username");
                    response = Response.status(Response.Status.UNAUTHORIZED).entity(object.toString()).build();
                }

            }  else {
                object = new JSONObject();
                object.put("Avviso", "Si è verificato un errore con il tuo account");
                response = Response.status(Response.Status.BAD_REQUEST).entity(object.toString()).build();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return response;
    } //username only

    public static int checkAdmin(String username, Connection connection) {
        String getOneSql = "SELECT * FROM users WHERE username = ?";
        int condition = 0; //0 se non c'è l'utente che esegue l'operazione nel DB
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getOneSql);
            preparedStatement.setString(1, username);
            result = preparedStatement.executeQuery();
            if (result.next()) {
                condition = 1; //1 se c'è l'utente che esegue l'operazione ma non si hanno i permessi
                if (result.getString("role").equals("admin"))
                    condition = 2; // se l'utente che vuole fare l'operazione è presente e ha i permessi
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