package hotelReviewBackend.Controller;

import hotelReviewBackend.JDBC.JDBC;
import hotelReviewBackend.Model.ReviewModel;
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

    //TODO fare la delete di una recensione

    public static Response addReview(String username, ReviewModel review) {
        Connection connection = JDBC.getInstance().getConnection();
        //Verifica se l' utente ha già inserito una recensione per l'hotel

        String checkReviewSql = "SELECT * from reviews WHERE users_username = ? AND hotel = ? AND cap = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(checkReviewSql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, review.getHotel());
            preparedStatement.setString(3, review.getCap());

            result = preparedStatement.executeQuery();

            if (!result.next()) {
                String insertReviewSql = "INSERT INTO reviews(title,text,hotel,valuation,upvote,downvote,users_username,cap) VALUES (?,?,?,?,?,?,?,?)";
                preparedStatement = connection.prepareStatement(insertReviewSql);
                preparedStatement.setString(1, review.getTitle());
                preparedStatement.setString(2, review.getText());
                preparedStatement.setString(3, review.getHotel());
                preparedStatement.setString(4, review.getValuation());
                preparedStatement.setInt(5, 0); //La recensione appena inserita non ha voti
                preparedStatement.setInt(6, 0);
                preparedStatement.setString(7, username);
                preparedStatement.setString(8, review.getCap());
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
                review.setValuation(result.getString("valuation"));
                review.setUpvote(result.getInt("upvote"));
                review.setDownvote(result.getInt("downvote"));
                review.setUsername(result.getString("users_username"));
                review.setCap(result.getString("cap"));
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

    public static List<ReviewModel> getAllReviewsByHotel(String hotel, String cap) {
        Connection connection = JDBC.getInstance().getConnection();
        List<ReviewModel> reviews = new ArrayList<>();

        String getAllSql = "SELECT * FROM reviews WHERE (hotel=? AND cap = ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getAllSql);
            preparedStatement.setString(1, hotel);
            preparedStatement.setString(2, cap);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                ReviewModel review = new ReviewModel();
                review.setTitle(resultSet.getString("title"));
                review.setText(resultSet.getString("text"));
                review.setHotel(resultSet.getString("hotel"));
                review.setValuation(resultSet.getString("valuation"));
                review.setUpvote(resultSet.getInt("upvote"));
                review.setDownvote(resultSet.getInt("downvote"));
                review.setUsername(resultSet.getString("users_username"));
                review.setCap(resultSet.getString("cap"));
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
                review.setTitle(resultSet.getString("title"));
                review.setText(resultSet.getString("text"));
                review.setHotel(resultSet.getString("hotel"));
                review.setValuation(resultSet.getString("valuation"));
                review.setUpvote(resultSet.getInt("upvote"));
                review.setDownvote(resultSet.getInt("downvote"));
                review.setUsername(resultSet.getString("users_username"));
                review.setCap(resultSet.getString("cap"));
                reviews.add(review);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        JDBC.closeConnection(connection);

        return reviews;
    }

    public static Response updateReviewById(String id, ReviewModel review) {
        Response response = null;
        Connection connection = JDBC.getInstance().getConnection();
        JSONObject object;
        String updateReviewSql = "UPDATE reviews SET title = ?, text = ?," +
                " hotel = ?, valuation = ?,cap = ? WHERE id = ?";
        //TODO Da vedere se hotel si deve modificare o no (si potrebbe usare api maps per trovare albergo)
//Nella richiesta bisogna includere tutti i campi anche quelli che non cambiano
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(updateReviewSql);
            preparedStatement.setString(1, review.getTitle());
            preparedStatement.setString(2, review.getText());
            preparedStatement.setString(3, review.getHotel());
            preparedStatement.setString(4, review.getValuation());
            preparedStatement.setString(5, review.getCap());
            preparedStatement.setString(6, id);
            preparedStatement.executeUpdate();
            object = new JSONObject();
            object.put("Avviso", "Recensione modificata correttamente"); //verificare se è meglio try/catch o aggiungere il metode in signature
            response = Response.status(Response.Status.OK).entity(object.toString()).build();

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
                    object.put("Avviso", "Downvote aggiornato"); //verificare se è meglio try/catch o aggiungere il metode in signature
                    response = Response.status(Response.Status.OK).entity(object.toString()).build();
                } else {
                    object = new JSONObject();
                    object.put("Avviso", "Errore durante l'aggiornamento"); //verificare se è meglio try/catch o aggiungere il metode in signature
                    response = Response.status(Response.Status.BAD_REQUEST).entity(object.toString()).build();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JDBC.closeConnection(connection);
        return response;
    }

}
