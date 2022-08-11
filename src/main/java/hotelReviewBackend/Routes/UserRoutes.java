package hotelReviewBackend.Routes;

import hotelReviewBackend.Model.LoginModel;
import hotelReviewBackend.controller.UserController;
import hotelReviewBackend.Model.UserModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/User")
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