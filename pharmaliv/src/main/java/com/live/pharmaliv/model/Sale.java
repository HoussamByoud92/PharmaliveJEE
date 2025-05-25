package com.live.pharmaliv.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Sale model class representing a sale in the system.
 * Maps to the 'sale' table in the database.
 */
public class Sale {
    private int id;
    private Integer customerId; // Can be null for anonymous customers
    private int userId;
    private BigDecimal totalAmount;
    private Timestamp createdAt;

    // Additional fields to store the associated customer and user (not in the database)
    private Customer customer;
    private User user;

    // List of sale items associated with this sale
    private List<SaleItem> saleItems = new ArrayList<>();

    // Default constructor
    public Sale() {
    }

    // Constructor with all fields except id and createdAt
    public Sale(Integer customerId, int userId, BigDecimal totalAmount) {
        this.customerId = customerId;
        this.userId = userId;
        this.totalAmount = totalAmount;
    }

    // Constructor with all fields
    public Sale(int id, Integer customerId, int userId, BigDecimal totalAmount, Timestamp createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null) {
            this.customerId = customer.getId();
        } else {
            this.customerId = null;
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userId = user.getId();
        }
    }

    public List<SaleItem> getSaleItems() {
        return saleItems;
    }

    public void setSaleItems(List<SaleItem> saleItems) {
        this.saleItems = saleItems;
    }

    /**
     * Add a sale item to this sale
     * @param saleItem The sale item to add
     */
    public void addSaleItem(SaleItem saleItem) {
        saleItems.add(saleItem);
        saleItem.setSaleId(this.id);

        // Recalculate total amount
        recalculateTotalAmount();
    }

    /**
     * Remove a sale item from this sale
     * @param saleItem The sale item to remove
     */
    public void removeSaleItem(SaleItem saleItem) {
        saleItems.remove(saleItem);

        // Recalculate total amount
        recalculateTotalAmount();
    }

    /**
     * Recalculate the total amount based on the sale items
     */
    public void recalculateTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (SaleItem item : saleItems) {
            total = total.add(item.getTotalPrice());
        }
        this.totalAmount = total;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", userId=" + userId +
                ", totalAmount=" + totalAmount +
                ", createdAt=" + createdAt +
                ", saleItems=" + saleItems.size() +
                '}';
    }
}
