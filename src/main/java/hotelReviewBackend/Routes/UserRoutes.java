package hotelReviewBackend.Routes;

import hotelReviewBackend.controller.UserController;
import hotelReviewBackend.Model.UserModel;

import javax.ws.rs.*;

@Path("/User")
public class UserRoutes {
    //Add user
    @POST
    @Path("/signin")
    @Produces("application/json")
    public UserModel addUser(UserModel user) {
        return UserController.addUser(user);
    }

    @GET
    @Produces("application/json")
    public UserModel getUser(@QueryParam("username") String username) {
        return UserController.getUserByUsername(username);
    }

}