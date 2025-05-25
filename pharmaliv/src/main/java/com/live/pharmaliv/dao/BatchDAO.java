package com.live.pharmaliv.dao;

import com.live.pharmaliv.model.Batch;
import java.util.List;
import java.sql.Date;

/**
 * Data Access Object interface for Batch entity.
 * Defines methods for CRUD operations on batches.
 */
public interface BatchDAO {
    
    /**
     * Find a batch by ID.
     * @param id The batch ID
     * @return The batch, or null if not found
     */
    Batch findById(int id);
    
    /**
     * Find batches by product ID.
     * @param productId The product ID
     * @return A list of batches for the product
     */
    List<Batch> findByProductId(int productId);
    
    /**
     * Find all batches.
     * @return A list of all batches
     */
    List<Batch> findAll();
    
    /**
     * Find batches expiring soon (within the next 30 days).
     * @return A list of batches expiring soon
     */
    List<Batch> findBatchesExpiringSoon();
    
    /**
     * Find batches that are expired.
     * @return A list of expired batches
     */
    List<Batch> findExpiredBatches();
    
    /**
     * Save a batch (create or update).
     * @param batch The batch to save
     * @return The saved batch with updated ID if created
     */
    Batch save(Batch batch);
    
    /**
     * Delete a batch by ID.
     * @param id The batch ID
     * @return true if deleted, false if not found
     */
    boolean delete(int id);
    
    /**
     * Update the quantity of a batch.
     * @param id The batch ID
     * @param quantity The new quantity
     * @return true if updated, false if not found
     */
    boolean updateQuantity(int id, int quantity);
    
    /**
     * Find batches by expiry date range.
     * @param startDate The start date
     * @param endDate The end date
     * @return A list of batches expiring in the date range
     */
    List<Batch> findByExpiryDateRange(Date startDate, Date endDate);
}