package hotelReviewBackend.Controller;

import hotelReviewBackend.JDBC.JDBC;
import hotelReviewBackend.Model.ReviewModel;
import hotelReviewBackend.Model.UserModel;
import org.json.JSONObject;

import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReviewController {
    static Response response = null;
    static JSONObject object;
    static ResultSet result = null;


    public static Response addReview(String username, ReviewModel review) {
        Connection connection = JDBC.getInstance().getConnection();
        //Verifica se l' utente ha già inserito una recensione per l'hotel

        String checkReviewSql = "SELECT * from reviews WHERE users_username = ? AND hotel = ? AND zipCode = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(checkReviewSql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, review.getHotel());
            preparedStatement.setString(3, review.getZipCode());

            result = preparedStatement.executeQuery();

            if (!result.next()) {
                String insertReviewSql = "INSERT INTO reviews(title,text,hotel,rating,upvote,downvote,users_username,zipCode) VALUES (?,?,?,?,?,?,?,?)";
                preparedStatement = connection.prepareStatement(insertReviewSql);
                preparedStatement.setString(1, review.getTitle());
                preparedStatement.setString(2, review.getText());
                preparedStatement.setString(3, review.getHotel());
                preparedStatement.setFloat(4, review.getRating());
                preparedStatement.setInt(5, 0); //La recensione appena inserita non ha voti
                preparedStatement.setInt(6, 0);
                preparedStatement.setString(7, username);
                preparedStatement.setString(8, review.getZipCode());
                preparedStatement.executeUpdate();
                object = new JSONObject();
                object.put("Avviso", "Recensione inserita correttamente"); //verificare se è meglio try/catch o aggiungere il metode in signature
                response = Response.status(Response.Status.OK).entity(object.toString()).build();
            } else {
                object = new JSONObject();
                object.put("Avviso", "Questo username ha già una recensione per questo hotel");
                response = Response.status(Response.Status.CONFLICT).entity(object.toString()).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JDBC.closeConnection(connection);
        return response;
    }

    public static ReviewModel getReviewById(String id) {

        Connection connection = JDBC.getInstance().getConnection();
        ReviewModel review = new ReviewModel();

        String getOneSql = "SELECT * FROM reviews WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getOneSql);
            preparedStatement.setString(1, id);
            ResultSet result = preparedStatement.executeQuery();


            while (result.next()) {
                review.setTitle(result.getString("title"));
                review.setText(result.getString("text"));
                review.setHotel(result.getString("hotel"));
                review.setRating(result.getFloat("rating"));
                review.setUpvote(result.getInt("upvote"));
                review.setDownvote(result.getInt("downvote"));
                review.setUsername(result.getString("users_username"));
                review.setZipCode(result.getString("zipCode"));
            }
            object = new JSONObject();
            object.put("Avviso", result); //verificare se è meglio try/catch o aggiungere il metode in signature
            response = Response.status(Response.Status.OK).entity(object.toString()).build();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        JDBC.closeConnection(connection);

        return review;
    }

    public static List<ReviewModel> getAllReviewsByHotel(String hotel, String zipCode) {
        Connection connection = JDBC.getInstance().getConnection();
        List<ReviewModel> reviews = new ArrayList<>();

        String getAllSql = "SELECT * FROM reviews WHERE (hotel=? AND zipCode = ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getAllSql);
            preparedStatement.setString(1, hotel);
            preparedStatement.setString(2, zipCode);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                ReviewModel review = new ReviewModel();
                review.setTitle(resultSet.getString("id"));
                review.setTitle(resultSet.getString("title"));
                review.setText(resultSet.getString("text"));
                review.setHotel(resultSet.getString("hotel"));
                review.setRating(resultSet.getFloat("rating"));
                review.setUpvote(resultSet.getInt("upvote"));
                review.setDownvote(resultSet.getInt("downvote"));
                review.setUsername(resultSet.getString("users_username"));
                review.setZipCode(resultSet.getString("zipCode"));
                reviews.add(review);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        JDBC.closeConnection(connection);

        return reviews;
    }

    public static List<ReviewModel> getAllReviewsByUsername(String username) {
        Connection connection = JDBC.getInstance().getConnection();
        List<ReviewModel> reviews = new ArrayList<>();

        String getAllSql = "SELECT * FROM reviews WHERE (users_username = ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getAllSql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                ReviewModel review = new ReviewModel();
                review.setId(resultSet.getInt("id"));
                review.setTitle(resultSet.getString("title"));
                review.setText(resultSet.getString("text"));
                review.setHotel(resultSet.getString("hotel"));
                review.setRating(resultSet.getFloat("rating"));
                review.setUpvote(resultSet.getInt("upvote"));
                review.setDownvote(resultSet.getInt("downvote"));
                review.setUsername(resultSet.getString("users_username"));
                review.setZipCode(resultSet.getString("zipCode"));
                reviews.add(review);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        JDBC.closeConnection(connection);

        return reviews;
    }

    public static Response updateReviewById(String id, ReviewModel review, String username) {

        Response response = null;
        Connection connection = JDBC.getInstance().getConnection();
        JSONObject object;
        String getUserFromReviewsSQL = "SELECT * from reviews WHERE users_username = ? AND id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getUserFromReviewsSQL);
            preparedStatement.setString(1,username);
            preparedStatement.setString(2,id);
            result = preparedStatement.executeQuery();
            if(result.next()) {
                String updateReviewSql = "UPDATE reviews SET title = ?, text = ?," +
                        " hotel = ?, rating = ?,zipCode = ? WHERE id = ?";

//Nella richiesta bisogna includere tutti i campi anche quelli che non cambiano

                preparedStatement = connection.prepareStatement(updateReviewSql);
                preparedStatement.setString(1, review.getTitle());
                preparedStatement.setString(2, review.getText());
                preparedStatement.setString(3, review.getHotel());
                preparedStatement.setFloat(4, review.getRating());
                preparedStatement.setString(5, review.getZipCode());
                preparedStatement.setString(6, id);
                preparedStatement.executeUpdate();
                object = new JSONObject();
                object.put("Avviso", "Recensione modificata correttamente"); //verificare se è meglio try/catch o aggiungere il metode in signature
                response = Response.status(Response.Status.OK).entity(object.toString()).build();
            }else{
                object = new JSONObject();
                object.put("Avviso", "Impossibile modificare la recensione"); //verificare se è meglio try/catch o aggiungere il metode in signature
                response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(object.toString()).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JDBC.closeConnection(connection);

        return response;
    }

    public static Response updateUpvoteOrDownvote(String id, String typevote, ReviewModel review) {
        Connection connection = JDBC.getInstance().getConnection();
        result = null;
        String getOneSql = "SELECT * FROM reviews WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getOneSql);
            preparedStatement.setString(1, id);
            result = preparedStatement.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        String updateUpVoteSql = "UPDATE reviews SET upvote= ? WHERE id = ?";
        String updateDownVoteSql = "UPDATE reviews SET downvote= ? WHERE id = ?";
        if (typevote.equals("upvote")) {
            try {
                if (result.next()) {
                    PreparedStatement preparedStatement = connection.prepareStatement(updateUpVoteSql);
                    preparedStatement.setInt(1, result.getInt("upvote") + 1);
                    preparedStatement.setString(2, id);
                    preparedStatement.executeUpdate();
                    object = new JSONObject();
                    object.put("Avviso", "Upvote aggiornato"); //verificare se è meglio try/catch o aggiungere il metode in signature
                    response = Response.status(Response.Status.OK).entity(object.toString()).build();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (typevote.equals("downvote")) {
            try {
                if (result.next()) {
                    PreparedStatement preparedStatement = connection.prepareStatement(updateDownVoteSql);
                    preparedStatement.setInt(1, result.getInt("downvote") + 1);
                    preparedStatement.setString(2, id);
                    preparedStatement.executeUpdate();
                    object = new JSONObject();
                    object.put("Avviso", "Downvote aggiornato");
                    response = Response.status(Response.Status.OK).entity(object.toString()).build();
                } else {
                    object = new JSONObject();
                    object.put("Avviso", "Errore durante l'aggiornamento");
                    response = Response.status(Response.Status.BAD_REQUEST).entity(object.toString()).build();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JDBC.closeConnection(connection);
        return response;
    }

    public static Response deleteReviewById(String id) {
        Connection connection = JDBC.getInstance().getConnection();

        String deleteOneSql = "DELETE FROM reviews WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(deleteOneSql);
            preparedStatement.setString(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            object = new JSONObject();

            if (rowsAffected > 0) {
                object.put("Avviso", "Recensione rimossa correttamente");
                response = Response.status(Response.Status.OK).entity(object.toString()).build();
            } else {
                object.put("Avviso", "Non è stata trovata nessuna recensione con questo id");
                response = Response.status(Response.Status.NOT_FOUND).entity(object.toString()).build();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;

    } //Admin only or the username

}
