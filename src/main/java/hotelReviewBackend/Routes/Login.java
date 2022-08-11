package hotelReviewBackend.Routes;

import hotelReviewBackend.Model.LoginModel;
import hotelReviewBackend.controller.UserController;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/login")
public class Login {

    @POST
    @Produces("application/json")
    public Response login(LoginModel user) {
        return UserController.login(user);
    }
}