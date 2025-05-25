package com.live.pharmaliv.dao;

import com.live.pharmaliv.model.Movement;
import java.util.List;
import java.sql.Date;

/**
 * Data Access Object interface for Movement entity.
 * Defines methods for CRUD operations on movements.
 */
public interface MovementDAO {
    
    /**
     * Find a movement by ID.
     * @param id The movement ID
     * @return The movement, or null if not found
     */
    Movement findById(int id);
    
    /**
     * Find movements by batch ID.
     * @param batchId The batch ID
     * @return A list of movements for the batch
     */
    List<Movement> findByBatchId(int batchId);
    
    /**
     * Find movements by user ID.
     * @param userId The user ID
     * @return A list of movements created by the user
     */
    List<Movement> findByUserId(int userId);
    
    /**
     * Find all movements.
     * @return A list of all movements
     */
    List<Movement> findAll();
    
    /**
     * Find movements by type.
     * @param type The movement type (entry, exit, adjustment)
     * @return A list of movements of the specified type
     */
    List<Movement> findByType(Movement.Type type);
    
    /**
     * Find movements by date range.
     * @param startDate The start date
     * @param endDate The end date
     * @return A list of movements in the date range
     */
    List<Movement> findByDateRange(Date startDate, Date endDate);
    
    /**
     * Save a movement (create or update).
     * @param movement The movement to save
     * @return The saved movement with updated ID if created
     */
    Movement save(Movement movement);
    
    /**
     * Delete a movement by ID.
     * @param id The movement ID
     * @return true if deleted, false if not found
     */
    boolean delete(int id);
    
    /**
     * Get the total quantity of entries for a batch.
     * @param batchId The batch ID
     * @return The total quantity of entries
     */
    int getTotalEntriesForBatch(int batchId);
    
    /**
     * Get the total quantity of exits for a batch.
     * @param batchId The batch ID
     * @return The total quantity of exits
     */
    int getTotalExitsForBatch(int batchId);
    
    /**
     * Get the total quantity of adjustments for a batch.
     * @param batchId The batch ID
     * @return The total quantity of adjustments (can be positive or negative)
     */
    int getTotalAdjustmentsForBatch(int batchId);
}