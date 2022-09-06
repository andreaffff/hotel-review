package hotelReviewBackend.Routes;

import hotelReviewBackend.Controller.UserController;
import hotelReviewBackend.Model.LoginModel;
import hotelReviewBackend.Model.UpdatePasswordOrUsernameModel;
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

    @GET
    @Path("/all")
    @Produces("application/json")
    public Response getAllUsers(@QueryParam("username") String username) {
        return UserController.getAllUsers(username);
    }

    @DELETE
    @Produces("application/json")
    public Response deleteUser(@QueryParam("username") String username, UserModel userToDelete) {
        return UserController.deleteUser(username, userToDelete);
    }

    @Path("/updateUser")
    @PUT
    @Produces("application/json")
    public Response updateUser(@QueryParam("username") String username, UserModel user) {
        return UserController.updateUser(username,user);
    }
    @Path("/updateRole")
    @PUT
    @Produces("application/json")
    public Response updateRole(@QueryParam("username") String username, UserModel userToUpdate) {
        return UserController.updateRole(username, userToUpdate);
    }

    @Path("/updatePassword")
    @PUT
    @Produces("application/json")
    public Response updatePassword(@QueryParam("username") String username, UpdatePasswordOrUsernameModel password) {
        return UserController.updatePassword(username, password);
    }
    @Path("/updateUsername")
    @PUT
    @Produces("application/json")
    public Response updateUsername(@QueryParam("username") String username, UpdatePasswordOrUsernameModel updateUsername) {
        return UserController.updateUsername(username, updateUsername);
    }
}