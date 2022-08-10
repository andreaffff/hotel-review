package hotelReviewBackend.Routes;

import hotelReviewBackend.controller.UserController;
import hotelReviewBackend.Model.UserModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/signin")
public class UserRoutes {
    //Add user
    @POST
    @Produces("application/json")
    public Response addUser(UserModel user) {
        return UserController.addUser(user);
    }
}