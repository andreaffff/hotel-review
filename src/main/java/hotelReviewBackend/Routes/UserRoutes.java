package hotelReviewBackend.Routes;

import hotelReviewBackend.Controller.UserController;
import hotelReviewBackend.Model.UserModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;


public class UserRoutes {
    //Add user
    @Path("/signin")
    @POST
    @Produces("application/json")
    public Response addUser(UserModel user) {
        return UserController.addUser(user);
    }

    @Path("/login")
    @POST
    @Produces("application/json")
    public Response login(String username, String password) {
        return UserController.login(username,password);
    };

}