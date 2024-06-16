package com.ibm.quarkus.academy.tmdb.connector.service;

import com.ibm.quarkus.academy.tmdb.connector.exceptioon.MovieNotFoundException;
import com.ibm.quarkus.academy.tmdb.connector.model.Movie;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.openapi.quarkus.tmdb_yaml.api.DefaultApi;
import org.openapi.quarkus.tmdb_yaml.model.*;
import org.jboss.logging.Logger;


import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@ApplicationScoped
public class TmdbService {
    @Inject
    @RestClient
    DefaultApi defaultApi;

    @Inject
    @Channel("discoveries-out")
    Emitter<Movie> movieEmitter;

    private static final Logger LOGGER = Logger.getLogger(TmdbService.class);

    @Retry(maxRetries = 3, delay = 200)
    @Fallback(fallbackMethod = "fallbackSearchMovie")
    @Timeout(5000)
    public List<Movie> searchMovie(String query) {
        LOGGER.infof("Searching movies with query: %s", query);
        SearchMovie200Response response = defaultApi.searchMovie(query, null, null, null, null, null, null);
        List<Movie> movies = mapToMovies(response.getResults());
        LOGGER.infof("Found %d movies", movies.size());
        return movies;
    }

    @Retry(maxRetries = 3, delay = 200)
    @Fallback(fallbackMethod = "fallbackGetMovieDetails")
    @Timeout(5000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 1000)
    public Movie getMovieDetails(Integer movieId) {
        LOGGER.infof("Getting movie details for ID: %d", movieId);
        try {
            MovieDetails200Response response = defaultApi.movieDetails(movieId, null, null);
            LOGGER.infof("Found movie details: %s", response);

            Movie movie = mapToMovie(response);
            LOGGER.infof("Fetched movie details: %s", movie);
            return movie;
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                LOGGER.warnf("Movie with ID %d not found", movieId);
                throw new MovieNotFoundException("Movie with ID " + movieId + " not found");
            } else {
                throw e;
            }
        }
    }

    @Retry(maxRetries = 3, delay = 200)
    @Fallback(fallbackMethod = "fallbackDiscoverMovies")
    @Timeout(5000)
    public List<Movie> discoverMovies() {
        int randomPage = new Random().nextInt(100) + 1;
        LOGGER.infof("Discovering movies, page: %d", randomPage);
        DiscoverMovie200Response response = defaultApi.discoverMovie(
                null, null, null, null, null, null, null, randomPage,
                null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null,
                null, null, null
        );
        List<Movie> movies = mapToMovies(response.getResults());
        LOGGER.infof("Discovered %d movies", movies.size());
        return movies;
    }

    private <T> List<Movie> mapToMovies(List<T> results) {
        return results.stream().map(this::mapToMovie).collect(Collectors.toList());
    }

    private Movie mapToMovie(Object result) {
        Movie movie = new Movie();
        if (result instanceof SearchMovie200ResponseResultsInner) {
            SearchMovie200ResponseResultsInner searchResult = (SearchMovie200ResponseResultsInner) result;
            movie.setId(searchResult.getId());
            movie.setTitle(searchResult.getTitle());
            movie.setReleaseDate(searchResult.getReleaseDate());
            movie.setOverview(searchResult.getOverview());
            movie.setOriginalLanguage(searchResult.getOriginalLanguage());
            movie.setOriginalTitle(searchResult.getOriginalTitle());
            movie.setPosterPath(searchResult.getPosterPath());
        } else if (result instanceof DiscoverMovie200ResponseResultsInner) {
            DiscoverMovie200ResponseResultsInner discoverResult = (DiscoverMovie200ResponseResultsInner) result;
            movie.setId(discoverResult.getId());
            movie.setTitle(discoverResult.getTitle());
            movie.setReleaseDate(discoverResult.getReleaseDate());
            movie.setOverview(discoverResult.getOverview());
            movie.setOriginalLanguage(discoverResult.getOriginalLanguage());
            movie.setOriginalTitle(discoverResult.getOriginalTitle());
            movie.setPosterPath(discoverResult.getPosterPath());
        } else if (result instanceof MovieDetails200Response) {
            MovieDetails200Response movieDetails = (MovieDetails200Response) result;
            movie.setId(movieDetails.getId());
            movie.setTitle(movieDetails.getTitle());
            movie.setReleaseDate(movieDetails.getReleaseDate());
            movie.setOverview(movieDetails.getOverview());
            movie.setOriginalLanguage(movieDetails.getOriginalLanguage());
            movie.setOriginalTitle(movieDetails.getOriginalTitle());
            movie.setPosterPath(movieDetails.getPosterPath());
        }
        return movie;
    }


    //Discover Scheduler
    @Scheduled(every = "30s")
    void discoverAndSendMovies() {
        try {
            List<Movie> movies = discoverMovies();
            if (!movies.isEmpty()) {
                Random random = new Random();
                for (int i = 0; i < 5; i++) {
                    int randomNum = random.nextInt(movies.size());
                    Movie randomMovie = movies.get(randomNum);
                    movieEmitter.send(randomMovie);
                    LOGGER.infof("Sent movie to Kafka: %s", randomMovie.getTitle());
                }
            } else {
                LOGGER.info("Movies List is empty!");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to discover and send movies", e);
        }
    }

    // Fallback methods
    public List<Movie> fallbackSearchMovie(String query) {
        LOGGER.warnf("Fallback method for searchMovie called for query: %s", query);
        return List.of();
    }

    public Movie fallbackGetMovieDetails(Integer movieId) {
        LOGGER.warnf("Fallback method for getMovieDetails called for movie ID: %d", movieId);
        return null;
    }

    public List<Movie> fallbackDiscoverMovies() {
        LOGGER.warn("Fallback method for discoverMovies called");
        return List.of();
    }
}