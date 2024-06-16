package com.ibm.quarkus.academy.ram.client;

import com.ibm.quarkus.academy.ram.model.Movie;
import jakarta.ws.rs.*;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.core.MediaType;
import java.util.List;

@RegisterRestClient(configKey = "tmdb-api")
public interface TmdbClient {

    @GET
    @Path("/tmdb/search/movie")
    @Produces(MediaType.APPLICATION_JSON)
    List<Movie> searchMovie(@QueryParam("query") String query);

    @GET
    @Path("/tmdb/movie/{movieId}")
    @Produces(MediaType.APPLICATION_JSON)
    Movie getMovieDetails(@PathParam("movieId") Integer movieId);
}