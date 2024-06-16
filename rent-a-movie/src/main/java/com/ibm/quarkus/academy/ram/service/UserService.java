package com.ibm.quarkus.academy.ram.service;

import com.ibm.quarkus.academy.ram.exception.UserNotFoundException;
import com.ibm.quarkus.academy.ram.exception.UserOperationException;
import com.ibm.quarkus.academy.ram.model.User;
import com.ibm.quarkus.academy.ram.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    private static final Logger LOGGER = Logger.getLogger(UserService.class);

    public List<User> listAllUsers() {
        LOGGER.info("Listing all users");
        List<User> users = userRepository.listAll();
        LOGGER.infof("Found %d users", users.size());
        return users;
    }

    public User findUserById(Long id) {
        LOGGER.infof("Finding user with ID: %d", id);
        User user = userRepository.findById(id);
        if (user == null) {
            LOGGER.warnf("User with ID %d not found", id);
            throw new UserNotFoundException("User with ID " + id + " not found");
        }
        LOGGER.infof("Found user: %s", user);
        return user;
    }

    @Transactional
    public User createUser(User user) {
        LOGGER.infof("Creating user: %s", user);
        try {
            user.setPassword(hashPassword(user.getPassword()));
            userRepository.persist(user);
            LOGGER.infof("Created user: %s", user);
        } catch (Exception e) {
            LOGGER.errorf("Error creating user: %s", e.getMessage());
            throw new UserOperationException("Error creating user: " + e.getMessage());
        }
        return user;
    }

    @Transactional
    public User updateUser(Long id, User user) {
        LOGGER.infof("Updating user with ID: %d", id);
        User existingUser = userRepository.findById(id);
        if (existingUser == null) {
            LOGGER.warnf("User with ID %d not found", id);
            throw new UserNotFoundException("User with ID " + id + " not found");
        }
        try {
            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(hashPassword(user.getPassword()));
            }
            userRepository.persist(existingUser);
            LOGGER.infof("Updated user: %s", existingUser);
        } catch (Exception e) {
            LOGGER.errorf("Error updating user: %s", e.getMessage());
            throw new UserOperationException("Error updating user: " + e.getMessage());
        }
        return existingUser;
    }

    @Transactional
    public boolean deleteUser(Long id) {
        LOGGER.infof("Deleting user with ID: %d", id);
        User user = userRepository.findById(id);
        if (user == null) {
            LOGGER.warnf("User with ID %d not found", id);
            throw new UserNotFoundException("User with ID " + id + " not found");
        }
        boolean deleted = userRepository.deleteById(id);
        if (deleted) {
            LOGGER.infof("Deleted user with ID: %d", id);
        } else {
            LOGGER.warnf("Failed to delete user with ID: %d", id);
        }
        return deleted;
    }

    public User login(String username, String password) {
        LOGGER.infof("Logging in user: %s", username);
        User user = userRepository.find("username", username).firstResult();
        if (user != null && user.getPassword().equals(hashPassword(password))) {
            LOGGER.infof("Login successful for user: %s", username);
            return user;
        } else {
            LOGGER.warnf("Invalid credentials for user: %s", username);
            return null;
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}