package com.ibm.quarkus.academy.ram.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.quarkus.academy.ram.client.TmdbClient;
import com.ibm.quarkus.academy.ram.exception.MovieAlreadyRentedException;
import com.ibm.quarkus.academy.ram.exception.MovieNotFoundException;
import com.ibm.quarkus.academy.ram.exception.UserNotFoundException;
import com.ibm.quarkus.academy.ram.model.Movie;
import com.ibm.quarkus.academy.ram.model.User;
import com.ibm.quarkus.academy.ram.repository.MovieRepository;
import com.ibm.quarkus.academy.ram.repository.UserRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class RamService {

    @Inject
    @RestClient
    TmdbClient tmdbClient;

    @Inject
    MovieRepository movieRepository;

    @Inject
    UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger LOGGER = Logger.getLogger(RamService.class);

    @Retry(maxRetries = 3, delay = 200)
    @Fallback(fallbackMethod = "fallbackSearchMovies")
    @Timeout(5000)
    public List<Movie> searchMovies(String query) {
        LOGGER.infof("Searching movies with query: %s", query);
        List<Movie> movies = tmdbClient.searchMovie(query);
        LOGGER.infof("Found %d movies", movies.size());
        return movies;
    }

    @Retry(maxRetries = 3, delay = 200)
    @Fallback(fallbackMethod = "fallbackSearchMoviesAsJson")
    @Timeout(5000)
    public String searchMoviesAsJson(String query) {
        try {
            List<Movie> movies = searchMovies(query);
            String jsonResult = objectMapper.writeValueAsString(movies);
            LOGGER.infof("Serialized movies to JSON: %s", jsonResult);
            return jsonResult;
        } catch (JsonProcessingException e) {
            LOGGER.error("Error serializing movies to JSON", e);
            throw new RuntimeException("Error serializing movies to JSON", e);
        }
    }

    @Retry(maxRetries = 3, delay = 200)
    @Fallback(fallbackMethod = "fallbackGetMovieById")
    @Timeout(5000)
    public Movie getMovieById(Integer id) {
        LOGGER.infof("Getting movie with ID: %d", id);
        Movie movie = tmdbClient.getMovieDetails(id);
        if (movie == null) {
            throw new MovieNotFoundException("Movie with ID " + id + " not found");
        }
        return movie;
    }

    @Retry(maxRetries = 3, delay = 200)
    @Fallback(fallbackMethod = "fallbackGetMoviesByPage")
    @Timeout(5000)
    public List<Movie> getMoviesByPage(int page) {
        int pageSize = 10;
        int offset = (page - 1) * pageSize;
        LOGGER.infof("Fetching movies with offset %d and limit %d", offset, pageSize);
        return movieRepository.find("ORDER BY id").page(Page.of(page - 1, pageSize)).list();
    }

    @Retry(maxRetries = 3, delay = 200)
    @Fallback(fallbackMethod = "fallbackRentMovie")
    @Timeout(5000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 1000)
    @Transactional
    public void rentMovie(Integer movieId, Long userId) {
        Movie movie = movieRepository.findById(movieId);

        if(movie != null && movie.getUser() != null) {
            throw new MovieAlreadyRentedException("Movie is already rented");
        }

        LOGGER.infof("Renting movie with ID: %d for user with ID: %d", movieId, userId);
        if(!(movie != null && movie.getUser() == null)) {
            movie = tmdbClient.getMovieDetails(movieId);
        }

        LOGGER.infof("Fetched movie details: %s", movie);

        User user = userRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }

        LOGGER.infof("Find user: %s", user);

        movie.setUser(user);
        LOGGER.infof("Setting user for movie: %s", movie);

        try {
            movie = movieRepository.getEntityManager().merge(movie);
            LOGGER.infof("Persisted movie: %s", movie);
        } catch (Exception e) {
            LOGGER.error("Error persisting movie", e);
            throw e;
        }
    }

    @Retry(maxRetries = 3, delay = 200)
    @Fallback(fallbackMethod = "fallbackGetRentedMovies")
    @Timeout(5000)
    public List<Movie> getRentedMovies(Long id) {
        User user = userRepository.findById(id);

        if(user == null) {
            throw new UserNotFoundException("User not found");
        }

        return user.getMovies();
    }

    @Retry(maxRetries = 3, delay = 200)
    @Fallback(fallbackMethod = "fallbackReturnMovie")
    @Timeout(5000)
    @Transactional
    public void returnMovie(Integer movieId) {
        Movie movieInDb = movieRepository.findById(movieId);

        if(movieInDb != null && movieInDb.getUser() == null) {
            throw new MovieNotFoundException("Movie with ID " + movieId + " not rented");
        }

        if (movieInDb != null) {
            movieInDb.setUser(null);
            LOGGER.infof("Setting user for movie: %s", movieInDb);

            try {
                movieInDb = movieRepository.getEntityManager().merge(movieInDb);
                LOGGER.infof("Persisted movie: %s", movieInDb);
            } catch (Exception e) {
                LOGGER.error("Error persisting movie", e);
                throw e;
            }
        } else {
            LOGGER.errorf("Movie with ID %d not found", movieId);
            throw new MovieNotFoundException("Movie with ID " + movieId + " not found");
        }

    }

    @Retry(maxRetries = 3, delay = 200)
    @Fallback(fallbackMethod = "fallbackGetAllMovies")
    @Timeout(5000)
    @Transactional
    public List<Movie> getAllMovies() {
        return movieRepository.listAll();
    }


    // Fallback methods
    public List<Movie> fallbackSearchMovies(String query) {
        LOGGER.warnf("Fallback method for searchMovies called for query: %s", query);
        return List.of();
    }

    public String fallbackSearchMoviesAsJson(String query) {
        LOGGER.warnf("Fallback method for searchMoviesAsJson called for query: %s", query);
        return "[]";
    }

    public void fallbackRentMovie(Integer movieId, Long userId) {
        LOGGER.warnf("Fallback method for rentMovie called for movie ID: %d and user ID: %d", movieId, userId);
    }

    public List<Movie> fallbackGetRentedMovies(Long id) {
        LOGGER.warnf("Fallback method for getRentedMovies called for user ID: %d", id);
        return List.of();
    }

    public void fallbackReturnMovie(Integer movieId) {
        LOGGER.warnf("Fallback method for returnMovie called for movie ID: %d", movieId);
    }

    public List<Movie> fallbackGetAllMovies() {
        LOGGER.warn("Fallback method for getAllMovies called");
        return List.of();
    }

    public Movie fallbackGetMovieById(Integer id) {
        LOGGER.warnf("Fallback method for getMovieById called for movie ID: %d", id);
        return null;
    }

    public List<Movie> fallbackGetMoviesByPage(int page) {
        LOGGER.warnf("Fallback method for getMoviesByPage called for page: %d", page);
        return List.of();
    }
}