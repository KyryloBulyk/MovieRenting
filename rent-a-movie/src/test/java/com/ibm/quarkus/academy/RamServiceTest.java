package com.ibm.quarkus.academy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.quarkus.academy.ram.client.TmdbClient;
import com.ibm.quarkus.academy.ram.exception.MovieNotFoundException;
import com.ibm.quarkus.academy.ram.exception.UserNotFoundException;
import com.ibm.quarkus.academy.ram.model.Movie;
import com.ibm.quarkus.academy.ram.model.User;
import com.ibm.quarkus.academy.ram.repository.MovieRepository;
import com.ibm.quarkus.academy.ram.repository.UserRepository;
import com.ibm.quarkus.academy.ram.service.RamService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@QuarkusTest
public class RamServiceTest {

    @InjectMocks
    RamService ramService;

    @Mock
    @RestClient
    TmdbClient tmdbClient;

    @Mock
    MovieRepository movieRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    EntityManager entityManager;

    @Mock
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(movieRepository.getEntityManager()).thenReturn(entityManager);
    }

    @Test
    @Transactional
    void testRentMovie() {
        Movie movie = new Movie();
        movie.setId(1);
        User user = new User();
        user.setId(1L);

        when(movieRepository.findById(anyInt())).thenReturn(null);
        when(userRepository.findById(anyLong())).thenReturn(user);
        when(tmdbClient.getMovieDetails(anyInt())).thenReturn(movie);
        when(entityManager.merge(any())).thenReturn(movie);

        ramService.rentMovie(1, 1L);

        // Verify method calls
        verify(movieRepository, times(1)).findById(anyInt());
        verify(userRepository, times(1)).findById(anyLong());
        verify(tmdbClient, times(1)).getMovieDetails(anyInt());
        verify(entityManager, times(1)).merge(any(Movie.class));
    }

    @Test
    void testSearchMovies() {
        List<Movie> movies = List.of(new Movie());
        when(tmdbClient.searchMovie(anyString())).thenReturn(movies);

        List<Movie> result = ramService.searchMovies("test");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(tmdbClient, times(1)).searchMovie(anyString());
    }

    @Test
    void testSearchMoviesAsJson() throws JsonProcessingException {
        List<Movie> movies = List.of(new Movie());
        String jsonResult = "[{}]";

        when(tmdbClient.searchMovie(anyString())).thenReturn(movies);
        when(objectMapper.writeValueAsString(movies)).thenReturn(jsonResult);

        String result = ramService.searchMoviesAsJson("test");

        assertNotNull(result);
        assertEquals(jsonResult, result);
        verify(tmdbClient, times(1)).searchMovie(anyString());
    }

    @Test
    void testGetRentedMovies() {
        User user = new User();
        user.setId(1L);
        user.setMovies(List.of(new Movie()));

        when(userRepository.findById(anyLong())).thenReturn(user);

        List<Movie> result = ramService.getRentedMovies(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void testGetRentedMovies_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(null);

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            ramService.getRentedMovies(1L);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    @Transactional
    void testReturnMovie() {
        Movie movie = new Movie();
        movie.setId(1);
        movie.setUser(new User());

        when(movieRepository.findById(anyInt())).thenReturn(movie);
        when(entityManager.merge(any())).thenReturn(movie);

        ramService.returnMovie(1);

        assertNull(movie.getUser());
        verify(movieRepository, times(1)).findById(anyInt());
        verify(entityManager, times(1)).merge(any(Movie.class));
    }

    @Test
    @Transactional
    void testReturnMovie_MovieNotRented() {
        Movie movie = new Movie();
        movie.setId(1);
        movie.setUser(null);

        when(movieRepository.findById(anyInt())).thenReturn(movie);

        Exception exception = assertThrows(MovieNotFoundException.class, () -> {
            ramService.returnMovie(1);
        });

        assertEquals("Movie with ID 1 not rented", exception.getMessage());
        verify(movieRepository, times(1)).findById(anyInt());
    }

    @Test
    @Transactional
    void testReturnMovie_MovieNotFound() {
        when(movieRepository.findById(anyInt())).thenReturn(null);

        Exception exception = assertThrows(MovieNotFoundException.class, () -> {
            ramService.returnMovie(1);
        });

        assertEquals("Movie with ID 1 not found", exception.getMessage());
        verify(movieRepository, times(1)).findById(anyInt());
    }

    @Test
    void testGetAllMovies() {
        List<Movie> movies = List.of(new Movie());
        when(movieRepository.listAll()).thenReturn(movies);

        List<Movie> result = ramService.getAllMovies();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(movieRepository, times(1)).listAll();
    }

}