package com.ibm.quarkus.academy;

import com.ibm.quarkus.academy.tmdb.connector.exceptioon.MovieNotFoundException;
import com.ibm.quarkus.academy.tmdb.connector.model.Movie;
import com.ibm.quarkus.academy.tmdb.connector.service.TmdbService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openapi.quarkus.tmdb_yaml.api.DefaultApi;
import org.openapi.quarkus.tmdb_yaml.model.*;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@QuarkusTest
public class TmdbServiceTest {

    @InjectMocks
    TmdbService tmdbService;

    @Mock
    @RestClient
    DefaultApi defaultApi;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchMovie() {
        SearchMovie200Response response = new SearchMovie200Response();
        SearchMovie200ResponseResultsInner result = new SearchMovie200ResponseResultsInner();
        result.setId(1);
        response.setResults(List.of(result));

        when(defaultApi.searchMovie(anyString(), any(), any(), any(), any(), any(), any())).thenReturn(response);

        List<Movie> movies = tmdbService.searchMovie("test");

        assertNotNull(movies);
        assertEquals(1, movies.size());
        verify(defaultApi, times(1)).searchMovie(anyString(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void testGetMovieDetails() {
        MovieDetails200Response response = new MovieDetails200Response();
        response.setId(1);

        when(defaultApi.movieDetails(anyInt(), any(), any())).thenReturn(response);

        Movie movie = tmdbService.getMovieDetails(1);

        assertNotNull(movie);
        assertEquals(1, movie.getId());
        verify(defaultApi, times(1)).movieDetails(anyInt(), any(), any());
    }

    @Test
    void testGetMovieDetails_NotFound() {
        WebApplicationException exception = new WebApplicationException("Not Found", Response.Status.NOT_FOUND);

        when(defaultApi.movieDetails(anyInt(), any(), any())).thenThrow(exception);

        Exception thrown = assertThrows(MovieNotFoundException.class, () -> {
            tmdbService.getMovieDetails(1);
        });

        assertEquals("Movie with ID 1 not found", thrown.getMessage());
        verify(defaultApi, times(1)).movieDetails(anyInt(), any(), any());
    }

    @Test
    void testDiscoverMovies() {
        DiscoverMovie200Response response = new DiscoverMovie200Response();
        DiscoverMovie200ResponseResultsInner result = new DiscoverMovie200ResponseResultsInner();
        result.setId(1);
        response.setResults(List.of(result));

        when(defaultApi.discoverMovie(any(), any(), any(), any(), any(), any(), any(), anyInt(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(response);

        List<Movie> movies = tmdbService.discoverMovies();

        assertNotNull(movies);
        assertEquals(1, movies.size());
        verify(defaultApi, times(1)).discoverMovie(any(), any(), any(), any(), any(), any(), any(), anyInt(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

}