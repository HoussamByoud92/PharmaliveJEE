package com.live.pharmaliv.dao;

import com.live.pharmaliv.model.Product;
import java.util.List;

/**
 * Data Access Object interface for Product entity.
 * Defines methods for CRUD operations on products.
 */
public interface ProductDAO {
    
    /**
     * Find a product by ID.
     * @param id The product ID
     * @return The product, or null if not found
     */
    Product findById(int id);
    
    /**
     * Find a product by code.
     * @param code The product code
     * @return The product, or null if not found
     */
    Product findByCode(String code);
    
    /**
     * Find all products.
     * @return A list of all products
     */
    List<Product> findAll();
    
    /**
     * Find products with stock below threshold.
     * @return A list of products with stock below threshold
     */
    List<Product> findProductsBelowThreshold();
    
    /**
     * Find products by name (partial match).
     * @param name The product name to search for
     * @return A list of products matching the name
     */
    List<Product> findByName(String name);
    
    /**
     * Save a product (create or update).
     * @param product The product to save
     * @return The saved product with updated ID if created
     */
    Product save(Product product);
    
    /**
     * Delete a product by ID.
     * @param id The product ID
     * @return true if deleted, false if not found
     */
    boolean delete(int id);
    
    /**
     * Get the total stock quantity for a product.
     * @param productId The product ID
     * @return The total stock quantity
     */
    int getTotalStockQuantity(int productId);
    
    /**
     * Get the best-selling products.
     * @param limit The maximum number of products to return
     * @return A list of the best-selling products
     */
    List<Product> getBestSellingProducts(int limit);
}