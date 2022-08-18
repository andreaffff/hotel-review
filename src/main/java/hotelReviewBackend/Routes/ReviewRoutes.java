package hotelReviewBackend.Routes;

import hotelReviewBackend.Model.ReviewModel;
import hotelReviewBackend.Controller.ReviewController;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/reviews")
public class ReviewRoutes {
    @POST
    @Produces("application/json")
    public Response addReview(@QueryParam("username")String username, ReviewModel review){
        return ReviewController.addReview(username,review);
    }
    @GET
    @Produces("application/json")
    public ReviewModel getReviewById(@QueryParam("id")String id) {
        return ReviewController.getReviewById(id);
    }

    @Path("/byhotel")
    @GET
    @Produces("application/json")
    public List<ReviewModel> getAllReviewsByHotel(@QueryParam("hotel")String hotel,@QueryParam("cap")String cap){
        return ReviewController.getAllReviewsByHotel(hotel,cap);
    }

    @Path("/byusername")
    @GET
    @Produces("application/json")
    public List<ReviewModel> getAllReviewsByUsername(@QueryParam("username")String username){
        return ReviewController.getAllReviewsByUsername(username);
    }

    @PUT
    @Produces("application/json")
    public Response updateReviewById(@QueryParam("id")String id, ReviewModel review) {
        return ReviewController.updateReviewById(id, review);
    }

    @PUT
    @Path("/vote")
    @Produces("application/json")
    public Response updateUpvoteOrDownvote(@QueryParam("id")String id, @QueryParam("typevote")String typevote,ReviewModel review){
        return ReviewController.updateUpvoteOrDownvote(id,typevote,review);
    }


}
