package com.live.pharmaliv.model;

import java.sql.Timestamp;

/**
 * Alert model class representing an alert in the system.
 * Maps to the 'alert' table in the database.
 */
public class Alert {
    private int id;
    private int productId;
    private Type type;
    private String message;
    private boolean isResolved;
    private Timestamp createdAt;
    private Timestamp resolvedAt;
    
    // Additional field to store the associated product (not in the database)
    private Product product;

    // Enum for alert types
    public enum Type {
        stock, expiry;
    }

    // Default constructor
    public Alert() {
    }

    // Constructor with all fields except id, createdAt, and resolvedAt
    public Alert(int productId, Type type, String message, boolean isResolved) {
        this.productId = productId;
        this.type = type;
        this.message = message;
        this.isResolved = isResolved;
    }

    // Constructor with all fields
    public Alert(int id, int productId, Type type, String message, boolean isResolved, Timestamp createdAt, Timestamp resolvedAt) {
        this.id = id;
        this.productId = productId;
        this.type = type;
        this.message = message;
        this.isResolved = isResolved;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isResolved() {
        return isResolved;
    }

    public void setResolved(boolean resolved) {
        isResolved = resolved;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Timestamp resolvedAt) {
        this.resolvedAt = resolvedAt;
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
     * Resolve the alert by setting isResolved to true and resolvedAt to the current time
     */
    public void resolve() {
        this.isResolved = true;
        this.resolvedAt = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "Alert{" +
                "id=" + id +
                ", productId=" + productId +
                ", type=" + type +
                ", message='" + message + '\'' +
                ", isResolved=" + isResolved +
                ", createdAt=" + createdAt +
                ", resolvedAt=" + resolvedAt +
                '}';
    }
}