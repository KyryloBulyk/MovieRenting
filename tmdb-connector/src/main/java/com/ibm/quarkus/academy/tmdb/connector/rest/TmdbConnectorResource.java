package com.ibm.quarkus.academy.tmdb.connector.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.quarkus.academy.tmdb.connector.exceptioon.CustomJsonProcessingException;
import com.ibm.quarkus.academy.tmdb.connector.exceptioon.MovieNotFoundException;
import com.ibm.quarkus.academy.tmdb.connector.model.Movie;
import com.ibm.quarkus.academy.tmdb.connector.service.TmdbService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/tmdb")
public class TmdbConnectorResource {

  @Inject
  TmdbService service;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private static final Logger LOGGER = Logger.getLogger(TmdbConnectorResource.class);

  @GET
  @Path("/search/movie")
  @Produces(MediaType.APPLICATION_JSON)
  public Response searchMovie(@QueryParam("query") String query) {
    LOGGER.infof("Searching movie with query: %s", query);
    List<Movie> result = service.searchMovie(query);

    try {
      if (result.isEmpty()) {
        LOGGER.warnf("No movies found for query: %s", query);
        throw new MovieNotFoundException("Movie for query " + query + " not found");
      }

      String jsonResult = objectMapper.writeValueAsString(result);
      LOGGER.infof("Movies found: %s", jsonResult);
      return Response.ok(jsonResult).build();
    } catch (JsonProcessingException jsonProcessingException) {
      LOGGER.errorf("Error processing JSON for query: %s", query, jsonProcessingException);
      throw new CustomJsonProcessingException("Error processing JSON", jsonProcessingException);
    }
  }

  @GET
  @Path("/movie/{movieId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getMovieDetails(@PathParam("movieId") Integer movieId) {
    LOGGER.infof("Getting movie details for ID: %d", movieId);
    Movie result = service.getMovieDetails(movieId);

    if (result == null) {
      LOGGER.warnf("Movie with ID %d not found", movieId);
      throw new MovieNotFoundException("Movie with ID " + movieId + " not found");
    }

    try {
      String jsonResult = objectMapper.writeValueAsString(result);
      LOGGER.infof("Movie details: %s", jsonResult);
      return Response.ok(jsonResult).build();
    } catch (JsonProcessingException e) {
      LOGGER.errorf("Error processing JSON for movie ID: %d", movieId, e);
      throw new CustomJsonProcessingException("Error processing JSON", e);
    }
  }
}