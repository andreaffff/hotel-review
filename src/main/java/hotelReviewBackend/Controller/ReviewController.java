package hotelReviewBackend.Controller;

import hotelReviewBackend.JDBC.JDBC;
import hotelReviewBackend.Model.ReviewModel;
import hotelReviewBackend.Model.UserModel;
import org.json.JSONException;
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
    public static Response addReview(String username,ReviewModel review) {
        Connection connection = JDBC.getInstance().getConnection();
        String insertReviewSql = "INSERT INTO reviews(title,text,hotel,valuation,upvote,downvote,users_username,cap) VALUES (?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(insertReviewSql);
            preparedStatement.setString(1, review.getTitle());
            preparedStatement.setString(2, review.getText());
            preparedStatement.setString(3, review.getHotel());
            preparedStatement.setString(4, review.getValuation());
            preparedStatement.setInt(5, 0); //La recensione appena inserita non ha voti
            preparedStatement.setInt(6, 0);
            preparedStatement.setString(7, username);
            preparedStatement.setString(8,review.getCap());
            preparedStatement.executeUpdate();
            System.out.println("review title= "+ review.getTitle());
            object = new JSONObject();
            object.put("Avviso", "Recensione inserita correttamente"); //verificare se è meglio try/catch o aggiungere il metode in signature
            response = Response.status(Response.Status.OK).entity(object.toString()).build();

        } catch (Exception e) {
            e.printStackTrace();
        }
        JDBC.closeConnection(connection);
        return response;
    }
    public static ReviewModel getReviewById(String id){

        Connection connection = JDBC.getInstance().getConnection();
        ReviewModel review = new ReviewModel();

        String getOneSql = "SELECT * FROM reviews WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getOneSql);
            preparedStatement.setString(1, id);
            ResultSet result = preparedStatement.executeQuery();
            System.out.println(id);
            while (result.next()) { //TODO come creare un messaggio di errore se il return deve essere un reviewModel
                System.out.println(result.getString("title"));
                review.setTitle(result.getString("title"));
                review.setText(result.getString("text"));
                review.setHotel(result.getString("hotel"));
                review.setValuation(result.getString("valuation"));
                review.setUpvote(result.getInt("upvote"));
                review.setDownvote(result.getInt("downvote"));
                review.setUsername(result.getString("users_username"));
                review.setCap(result.getString("cap"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        JDBC.closeConnection(connection);

        return review;
    }
    public static List<ReviewModel> getAllReviews(){
        Connection connection = JDBC.getInstance().getConnection();
        List<ReviewModel> reviews = new ArrayList<>();

        String getOneSql = "SELECT * FROM reviews";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getOneSql);
            ResultSet resultSet = preparedStatement.executeQuery();


            while (resultSet.next()) {
                ReviewModel review = new ReviewModel();
                System.out.println(resultSet.getString("title"));
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
    public static Response updateReviewById(String id, ReviewModel review){
        Response response = null;
        Connection connection = JDBC.getInstance().getConnection();
        JSONObject object;
        String updateReviewSql = "UPDATE reviews SET title = ?, text = ?," +
                " hotel = ?, valuation = ?,cap = ? WHERE id = ?";
        //Da vedere se hotel si deve modificare o no (si potrebbe usare api maps per trovare albergo)
//Nella richiesta bisogna includere tutti i campi anche quelli che non cambiano
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(updateReviewSql);
            preparedStatement.setString(1, review.getTitle());
            preparedStatement.setString(2, review.getText());
            preparedStatement.setString(3, review.getHotel());
            preparedStatement.setString(4, review.getValuation());
            preparedStatement.setString(5,review.getCap());
            preparedStatement.setString(6,id);
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
    public static Response updateUpvoteOrDownvote(String id,String typevote, ReviewModel review){
        Connection connection = JDBC.getInstance().getConnection();
        result = null;
        String getOneSql = "SELECT * FROM reviews WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getOneSql);
            preparedStatement.setString(1, id);
            result = preparedStatement.executeQuery();

        }catch(SQLException e){
            e.printStackTrace();
        }

        String updateUpVoteSql = "UPDATE reviews SET upvote= ? WHERE id = ?";
        String updateDownVoteSql = "UPDATE reviews SET downvote= ? WHERE id = ?";
        if(typevote.equals("upvote")){
            try {
                while(!result.next()){
                    //Appena viene eseguita la prima query va avanti
                }
                PreparedStatement preparedStatement = connection.prepareStatement(updateUpVoteSql);
                preparedStatement.setInt(1,result.getInt("upvote")+1);
                preparedStatement.setString(2,id);
                preparedStatement.executeUpdate();
                object = new JSONObject();
                object.put("Avviso", "Upvote aggiornato"); //verificare se è meglio try/catch o aggiungere il metode in signature
                response = Response.status(Response.Status.OK).entity(object.toString()).build();
            }catch(Exception e){
                e.printStackTrace();
            }
        }else if (typevote.equals("downvote")){
            try {
                while(!result.next()){
                    //Appena viene eseguita la prima query va avanti
                }
                PreparedStatement preparedStatement = connection.prepareStatement(updateDownVoteSql);
                preparedStatement.setInt(1,result.getInt("downvote")+1);
                preparedStatement.setString(2,id);
                preparedStatement.executeUpdate();
                object = new JSONObject();
                object.put("Avviso", "Downvote aggiornato"); //verificare se è meglio try/catch o aggiungere il metode in signature
                response = Response.status(Response.Status.OK).entity(object.toString()).build();
            }catch(Exception e){
                e.printStackTrace();
            }
        }else{
            object = new JSONObject();
            try {
                object.put("Avviso", "Errore durante l'aggiornamento"); //verificare se è meglio try/catch o aggiungere il metode in signature
            }catch(JSONException e){
                e.printStackTrace();
            }
            response = Response.status(Response.Status.BAD_REQUEST).entity(object.toString()).build();
        }
        JDBC.closeConnection(connection);
        return response;
    }
}
