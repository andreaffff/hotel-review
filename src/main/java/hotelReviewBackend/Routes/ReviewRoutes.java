package hotelReviewBackend.Routes;

import hotelReviewBackend.Model.ReviewModel;
import hotelReviewBackend.Controller.ReviewController;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/reviews")
public class ReviewRoutes {
    @POST
    @Produces("application/json")
    public Response addReview(@QueryParam("username") String username, ReviewModel review){
        return ReviewController.addReview(username,review);
    }

}
