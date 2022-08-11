package hotelReviewBackend.Controller;

import hotelReviewBackend.JDBC.JDBC;
import hotelReviewBackend.Model.ReviewModel;
import org.json.JSONObject;

import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReviewController {
    public static Response addReview(String username,ReviewModel review) {
        Connection connection = JDBC.getInstance().getConnection();
        JSONObject object;
        Response response = null;
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
        return response;
    }

}
