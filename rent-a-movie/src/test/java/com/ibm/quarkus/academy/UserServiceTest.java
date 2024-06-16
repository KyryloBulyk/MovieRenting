package com.ibm.quarkus.academy;

import com.ibm.quarkus.academy.ram.exception.UserNotFoundException;
import com.ibm.quarkus.academy.ram.exception.UserOperationException;
import com.ibm.quarkus.academy.ram.model.User;
import com.ibm.quarkus.academy.ram.repository.UserRepository;
import com.ibm.quarkus.academy.ram.service.UserService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@QuarkusTest
public class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    Logger logger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListAllUsers() {
        List<User> users = List.of(new User(), new User());
        when(userRepository.listAll()).thenReturn(users);

        List<User> result = userService.listAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).listAll();
    }

    @Test
    void testFindUserById() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(user);

        User result = userService.findUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void testFindUserById_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(null);

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.findUserById(1L);
        });

        assertEquals("User with ID 1 not found", exception.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    @Transactional
    void testCreateUser() {
        User user = new User();
        user.setEmail("mail@example.com");
        user.setUsername("username");
        user.setPassword("password");
        user.setId(1L);

        userService.createUser(user);

        verify(userRepository, times(1)).persist(user);
    }

    @Test
    @Transactional
    void testCreateUser_Exception() {
        User user = new User();
        user.setEmail("mail@example.com");
        user.setUsername("username");
        user.setPassword("password");
        user.setId(1L);

        doThrow(new RuntimeException("DB Error")).when(userRepository).persist(any(User.class));

        Exception exception = assertThrows(UserOperationException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Error creating user: DB Error", exception.getMessage());
        verify(userRepository, times(1)).persist(any(User.class));
    }

    @Test
    @Transactional
    void testUpdateUser() {
        User existingUser = new User();
        existingUser.setId(1L);
        User newUser = new User();
        newUser.setUsername("newUsername");
        newUser.setEmail("newEmail");
        newUser.setPassword("newPassword");

        when(userRepository.findById(anyLong())).thenReturn(existingUser);

        User result = userService.updateUser(1L, newUser);

        assertNotNull(result);
        assertEquals("newUsername", result.getUsername());
        assertEquals("newEmail", result.getEmail());
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).persist(existingUser);
    }

    @Test
    @Transactional
    void testUpdateUser_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(null);

        User newUser = new User();
        newUser.setUsername("newUsername");
        newUser.setEmail("newEmail");
        newUser.setPassword("newPassword");

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(1L, newUser);
        });

        assertEquals("User with ID 1 not found", exception.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    @Transactional
    void testUpdateUser_Exception() {
        User existingUser = new User();
        existingUser.setId(1L);
        User newUser = new User();
        newUser.setUsername("newUsername");
        newUser.setEmail("newEmail");
        newUser.setPassword("newPassword");

        when(userRepository.findById(anyLong())).thenReturn(existingUser);
        doThrow(new RuntimeException("DB Error")).when(userRepository).persist(any(User.class));

        Exception exception = assertThrows(UserOperationException.class, () -> {
            userService.updateUser(1L, newUser);
        });

        assertEquals("Error updating user: DB Error", exception.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).persist(existingUser);
    }

    @Test
    @Transactional
    void testDeleteUser() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(anyLong())).thenReturn(user);
        when(userRepository.deleteById(anyLong())).thenReturn(true);

        boolean result = userService.deleteUser(1L);

        assertTrue(result);
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).deleteById(anyLong());
    }

    @Test
    @Transactional
    void testDeleteUser_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(null);

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(1L);
        });

        assertEquals("User with ID 1 not found", exception.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
    }
}