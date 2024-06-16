package com.ibm.quarkus.academy.ram.rest;

import com.ibm.quarkus.academy.ram.model.User;
import com.ibm.quarkus.academy.ram.service.UserService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/users")
public class UserResource {

    @Inject
    UserService userService;

    private static final Logger LOGGER = Logger.getLogger(UserResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "List all users", description = "Returns a list of all users")
    @APIResponse(responseCode = "200", description = "List of users", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    public List<User> listAllUsers() {
        LOGGER.info("Listing all users");
        List<User> users = userService.listAllUsers();
        LOGGER.infof("Found %d users", users.size());
        return users;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Find user by ID", description = "Returns a single user by ID")
    @APIResponse(responseCode = "200", description = "User found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    @APIResponse(responseCode = "404", description = "User not found")
    public User findUserById(@PathParam("id") Long id) {
        LOGGER.infof("Finding user by ID: %d", id);
        User user = userService.findUserById(id);
        if (user == null) {
            LOGGER.warnf("User with ID %d not found", id);
            throw new NotFoundException("User not found");
        }
        LOGGER.infof("Found user: %s", user);
        return user;
    }

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a new user", description = "Creates a new user")
    @APIResponse(responseCode = "201", description = "User created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    public Response createUser(@Valid User user) {
        LOGGER.infof("Creating user: %s", user);
        User createdUser = userService.createUser(user);
        LOGGER.infof("Created user: %s", createdUser);
        return Response.ok(createdUser).status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update an existing user", description = "Updates an existing user by ID")
    @APIResponse(responseCode = "200", description = "User updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    @APIResponse(responseCode = "404", description = "User not found")
    public User updateUser(@PathParam("id") Long id, @Valid User user) {
        LOGGER.infof("Updating user with ID: %d, data: %s", id, user);
        User updatedUser = userService.updateUser(id, user);
        if (updatedUser == null) {
            LOGGER.warnf("User with ID %d not found for update", id);
            throw new NotFoundException("User not found");
        }
        LOGGER.infof("Updated user: %s", updatedUser);
        return updatedUser;
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete a user", description = "Deletes a user by ID")
    @APIResponse(responseCode = "204", description = "User deleted")
    @APIResponse(responseCode = "404", description = "User not found")
    public Response deleteUser(@PathParam("id") Long id) {
        LOGGER.infof("Deleting user with ID: %d", id);
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            LOGGER.infof("Deleted user with ID: %d", id);
            return Response.noContent().build();
        } else {
            LOGGER.warnf("User with ID %d not found for deletion", id);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Login a user", description = "Logs in a user with the provided credentials")
    @APIResponse(responseCode = "200", description = "Login successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    @APIResponse(responseCode = "401", description = "Invalid credentials")
    public Response login(@Valid User user) {
        LOGGER.infof("Logging in user: %s", user.getUsername());
        User loggedInUser = userService.login(user.getUsername(), user.getPassword());
        if (loggedInUser != null) {
            LOGGER.infof("Login successful for user: %s", loggedInUser.getUsername());
            return Response.ok(loggedInUser).build();
        } else {
            LOGGER.warnf("Invalid credentials for user: %s", user.getUsername());
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
}