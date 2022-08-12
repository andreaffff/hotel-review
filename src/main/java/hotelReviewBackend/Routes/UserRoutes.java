package hotelReviewBackend.Routes;

import hotelReviewBackend.Controller.UserController;
import hotelReviewBackend.Model.LoginModel;
import hotelReviewBackend.Model.UserModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/user")
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
    public Response login(LoginModel user) {
        return UserController.login(user);
    }

    @GET
    @Produces("application/json")
    public UserModel getUser(@QueryParam("username") String username) {
        return UserController.getUserByUsername(username);
    }

}