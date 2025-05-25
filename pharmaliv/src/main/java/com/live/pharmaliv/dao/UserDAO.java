package com.live.pharmaliv.dao;

import com.live.pharmaliv.model.User;
import java.util.List;

/**
 * Data Access Object interface for User entity.
 * Defines methods for CRUD operations on users.
 */
public interface UserDAO {
    
    /**
     * Find a user by ID.
     * @param id The user ID
     * @return The user, or null if not found
     */
    User findById(int id);
    
    /**
     * Find a user by username.
     * @param username The username
     * @return The user, or null if not found
     */
    User findByUsername(String username);
    
    /**
     * Find all users.
     * @return A list of all users
     */
    List<User> findAll();
    
    /**
     * Save a user (create or update).
     * @param user The user to save
     * @return The saved user with updated ID if created
     */
    User save(User user);
    
    /**
     * Delete a user by ID.
     * @param id The user ID
     * @return true if deleted, false if not found
     */
    boolean delete(int id);
    
    /**
     * Authenticate a user with username and password.
     * @param username The username
     * @param password The password (plain text)
     * @return The authenticated user, or null if authentication fails
     */
    User authenticate(String username, String password);
}