package com.live.pharmaliv.dao;

import com.live.pharmaliv.model.Customer;
import java.util.List;

/**
 * Data Access Object interface for Customer entity.
 * Defines methods for CRUD operations on customers.
 */
public interface CustomerDAO {
    
    /**
     * Find a customer by ID.
     * @param id The customer ID
     * @return The customer, or null if not found
     */
    Customer findById(int id);
    
    /**
     * Find all customers.
     * @return A list of all customers
     */
    List<Customer> findAll();
    
    /**
     * Find customers by name (partial match).
     * @param name The customer name to search for
     * @return A list of customers matching the name
     */
    List<Customer> findByName(String name);
    
    /**
     * Find a customer by email.
     * @param email The customer email
     * @return The customer, or null if not found
     */
    Customer findByEmail(String email);
    
    /**
     * Find a customer by phone.
     * @param phone The customer phone
     * @return The customer, or null if not found
     */
    Customer findByPhone(String phone);
    
    /**
     * Save a customer (create or update).
     * @param customer The customer to save
     * @return The saved customer with updated ID if created
     */
    Customer save(Customer customer);
    
    /**
     * Delete a customer by ID.
     * @param id The customer ID
     * @return true if deleted, false if not found
     */
    boolean delete(int id);
}