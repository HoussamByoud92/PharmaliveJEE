package com.live.pharmaliv.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Batch model class representing a batch of products in the system.
 * Maps to the 'batch' table in the database.
 */
public class Batch {
    private int id;
    private int productId;
    private String batchNumber;
    private int quantity;
    private Date expiryDate;
    private BigDecimal purchasePrice;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Additional field to store the associated product (not in the database)
    private Product product;

    // Default constructor
    public Batch() {
    }

    // Constructor with all fields except id, createdAt, and updatedAt
    public Batch(int productId, String batchNumber, int quantity, Date expiryDate, BigDecimal purchasePrice) {
        this.productId = productId;
        this.batchNumber = batchNumber;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.purchasePrice = purchasePrice;
    }

    // Constructor with all fields
    public Batch(int id, int productId, String batchNumber, int quantity, Date expiryDate, BigDecimal purchasePrice, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.productId = productId;
        this.batchNumber = batchNumber;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.purchasePrice = purchasePrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
        if (product != null) {
            this.productId = product.getId();
        }
    }
    
    /**
     * Check if the batch is expired
     * @return true if the batch is expired, false otherwise
     */
    public boolean isExpired() {
        return expiryDate.before(new Date(System.currentTimeMillis()));
    }
    
    /**
     * Check if the batch is expiring soon (within the next 30 days)
     * @return true if the batch is expiring soon, false otherwise
     */
    public boolean isExpiringSoon() {
        long thirtyDaysInMillis = 30L * 24 * 60 * 60 * 1000;
        Date thirtyDaysFromNow = new Date(System.currentTimeMillis() + thirtyDaysInMillis);
        return expiryDate.after(new Date(System.currentTimeMillis())) && expiryDate.before(thirtyDaysFromNow);
    }

    @Override
    public String toString() {
        return "Batch{" +
                "id=" + id +
                ", productId=" + productId +
                ", batchNumber='" + batchNumber + '\'' +
                ", quantity=" + quantity +
                ", expiryDate=" + expiryDate +
                ", purchasePrice=" + purchasePrice +
                '}';
    }
}