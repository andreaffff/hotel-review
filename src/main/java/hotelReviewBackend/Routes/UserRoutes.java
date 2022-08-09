package hotelReviewBackend.Routes;

import hotelReviewBackend.controller.UserController;
import hotelReviewBackend.Model.UserModel;

import javax.ws.rs.*;

@Path("/signin")
public class UserRoutes {
    //Add user
    @POST
    @Produces("application/json")
    public UserModel addUser(UserModel user) {
        return UserController.addUser(user);
    }
}