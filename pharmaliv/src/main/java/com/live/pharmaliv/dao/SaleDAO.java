package com.live.pharmaliv.dao;

import com.live.pharmaliv.model.Sale;
import com.live.pharmaliv.model.SaleItem;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Data Access Object interface for Sale entity.
 * Defines methods for CRUD operations on sales.
 */
public interface SaleDAO {
    
    /**
     * Find a sale by ID.
     * @param id The sale ID
     * @return The sale, or null if not found
     */
    Sale findById(int id);
    
    /**
     * Find all sales.
     * @return A list of all sales
     */
    List<Sale> findAll();
    
    /**
     * Find sales by customer ID.
     * @param customerId The customer ID
     * @return A list of sales for the customer
     */
    List<Sale> findByCustomerId(int customerId);
    
    /**
     * Find sales by user ID.
     * @param userId The user ID
     * @return A list of sales made by the user
     */
    List<Sale> findByUserId(int userId);
    
    /**
     * Find sales by date.
     * @param date The date
     * @return A list of sales made on the date
     */
    List<Sale> findByDate(LocalDate date);
    
    /**
     * Find sales between two dates.
     * @param startDate The start date
     * @param endDate The end date
     * @return A list of sales made between the dates
     */
    List<Sale> findBetweenDates(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get the total sales amount for a date.
     * @param date The date
     * @return The total sales amount
     */
    BigDecimal getTotalSalesForDate(LocalDate date);
    
    /**
     * Get the total sales amount between two dates.
     * @param startDate The start date
     * @param endDate The end date
     * @return The total sales amount
     */
    BigDecimal getTotalSalesBetweenDates(LocalDate startDate, LocalDate endDate);
    
    /**
     * Save a sale (create or update).
     * @param sale The sale to save
     * @return The saved sale with updated ID if created
     */
    Sale save(Sale sale);
    
    /**
     * Delete a sale by ID.
     * @param id The sale ID
     * @return true if deleted, false if not found
     */
    boolean delete(int id);
    
    /**
     * Find a sale item by ID.
     * @param id The sale item ID
     * @return The sale item, or null if not found
     */
    SaleItem findSaleItemById(int id);
    
    /**
     * Find sale items by sale ID.
     * @param saleId The sale ID
     * @return A list of sale items for the sale
     */
    List<SaleItem> findSaleItemsBySaleId(int saleId);
    
    /**
     * Save a sale item (create or update).
     * @param saleItem The sale item to save
     * @return The saved sale item with updated ID if created
     */
    SaleItem saveSaleItem(SaleItem saleItem);
    
    /**
     * Delete a sale item by ID.
     * @param id The sale item ID
     * @return true if deleted, false if not found
     */
    boolean deleteSaleItem(int id);
}