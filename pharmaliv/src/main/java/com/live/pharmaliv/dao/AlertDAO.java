package com.live.pharmaliv.dao;

import com.live.pharmaliv.model.Alert;
import java.util.List;

/**
 * Data Access Object interface for Alert entity.
 * Defines methods for CRUD operations on alerts.
 */
public interface AlertDAO {
    
    /**
     * Find an alert by ID.
     * @param id The alert ID
     * @return The alert, or null if not found
     */
    Alert findById(int id);
    
    /**
     * Find all alerts.
     * @return A list of all alerts
     */
    List<Alert> findAll();
    
    /**
     * Find alerts by product ID.
     * @param productId The product ID
     * @return A list of alerts for the product
     */
    List<Alert> findByProductId(int productId);
    
    /**
     * Find alerts by type.
     * @param type The alert type
     * @return A list of alerts of the specified type
     */
    List<Alert> findByType(Alert.Type type);
    
    /**
     * Find unresolved alerts.
     * @return A list of unresolved alerts
     */
    List<Alert> findUnresolved();
    
    /**
     * Find recent alerts.
     * @param limit The maximum number of alerts to return
     * @return A list of the most recent alerts
     */
    List<Alert> findRecentAlerts(int limit);
    
    /**
     * Count alerts by type.
     * @param type The alert type
     * @return The number of alerts of the specified type
     */
    int countAlertsByType(Alert.Type type);
    
    /**
     * Save an alert (create or update).
     * @param alert The alert to save
     * @return The saved alert with updated ID if created
     */
    Alert save(Alert alert);
    
    /**
     * Delete an alert by ID.
     * @param id The alert ID
     * @return true if deleted, false if not found
     */
    boolean delete(int id);
    
    /**
     * Resolve an alert.
     * @param id The alert ID
     * @return true if resolved, false if not found
     */
    boolean resolveAlert(int id);
    
    /**
     * Create stock alerts for products below threshold.
     * This method checks all products and creates alerts for those with stock below threshold.
     * @return The number of alerts created
     */
    int createStockAlerts();
    
    /**
     * Create expiry alerts for batches expiring soon.
     * This method checks all batches and creates alerts for those expiring within the specified days.
     * @param daysThreshold The number of days threshold for expiry
     * @return The number of alerts created
     */
    int createExpiryAlerts(int daysThreshold);
}