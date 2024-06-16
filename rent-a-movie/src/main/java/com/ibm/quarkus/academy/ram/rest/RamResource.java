package com.ibm.quarkus.academy.ram.rest;

import com.ibm.quarkus.academy.ram.model.Movie;
import com.ibm.quarkus.academy.ram.service.RamService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/ram")
public class RamResource {

    @Inject
    RamService ramService;

    private static final Logger LOGGER = Logger.getLogger(RamResource.class);

    @GET
    @Path("/search/movie")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Search for movies", description = "Search for movies based on query")
    @APIResponse(responseCode = "200", description = "Movies found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Movie.class)))
    public Response searchMovies(@QueryParam("query") String query) {
        LOGGER.infof("Searching movies with query: %s", query);
        String jsonResult = ramService.searchMoviesAsJson(query);
        LOGGER.infof("Found movies: %s", jsonResult);
        return Response.ok(jsonResult).build();
    }

    @GET
    @Path("/movies")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all movies", description = "Fetch all movies from the database")
    @APIResponse(responseCode = "200", description = "Movies retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Movie.class)))
    public Response getAllMovies() {
        LOGGER.info("Fetching all movies");
        List<Movie> movies = ramService.getAllMovies();
        LOGGER.infof("Found %d movies", movies.size());
        return Response.ok(movies).build();
    }

    @GET
    @Path("/movies/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get movie by ID", description = "Fetch a movie by its ID")
    @APIResponse(responseCode = "200", description = "Movie found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Movie.class)))
    @APIResponse(responseCode = "404", description = "Movie not found")
    public Response getMovieById(@PathParam("id") Integer id) {
        LOGGER.infof("Fetching movie with ID: %d", id);
        Movie movie = ramService.getMovieById(id);
        if (movie != null) {
            LOGGER.infof("Found movie: %s", movie);
            return Response.ok(movie).build();
        } else {
            LOGGER.warnf("Movie with ID %d not found", id);
            return Response.status(Response.Status.NOT_FOUND).entity("Movie not found").build();
        }
    }

    @GET
    @Path("/movies/page/{page}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get paginated movies", description = "Fetch a specific page of movies from the database")
    @APIResponse(responseCode = "200", description = "Movies retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Movie.class)))
    public Response getMoviesByPage(@PathParam("page") int page) {
        LOGGER.infof("Fetching movies for page: %d", page);
        List<Movie> movies = ramService.getMoviesByPage(page);
        LOGGER.infof("Found %d movies for page %d", movies.size(), page);
        return Response.ok(movies).build();
    }

    @GET
    @Path("/user/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get rented movies for user", description = "Fetch all movies rented by a user")
    @APIResponse(responseCode = "200", description = "Rented movies retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Movie.class)))
    @APIResponse(responseCode = "404", description = "User not found")
    public Response getRentedMoviesForUser(@PathParam("id") Long id) {
        LOGGER.infof("Fetching rented movies for user with ID: %d", id);
        List<Movie> rentedMovies = ramService.getRentedMovies(id);
        LOGGER.infof("Found %d rented movies for user ID: %d", rentedMovies.size(), id);
        return Response.ok(rentedMovies).build();
    }

    @POST
    @Path("/rent/{id_movie}/user/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Rent a movie", description = "Rent a movie for a user")
    @APIResponse(responseCode = "200", description = "Movie rented successfully")
    @APIResponse(responseCode = "404", description = "User or movie not found")
    public Response rentMovie(@PathParam("id_movie") Integer movieId, @PathParam("id") Long userId) {
        LOGGER.infof("Renting movie with ID: %d for user with ID: %d", movieId, userId);
        ramService.rentMovie(movieId, userId);
        LOGGER.infof("Successfully rented movie ID: %d for user ID: %d", movieId, userId);
        return Response.ok().build();
    }

    @POST
    @Path("/return/{id}")
    @Operation(summary = "Return a rented movie", description = "Return a rented movie")
    @APIResponse(responseCode = "200", description = "Movie returned successfully")
    @APIResponse(responseCode = "404", description = "Movie not found")
    public Response returnMovie(@PathParam("id") Integer id) {
        LOGGER.infof("Returning movie with ID: %d", id);
        ramService.returnMovie(id);
        LOGGER.infof("Successfully returned movie ID: %d", id);
        return Response.ok().build();
    }
}