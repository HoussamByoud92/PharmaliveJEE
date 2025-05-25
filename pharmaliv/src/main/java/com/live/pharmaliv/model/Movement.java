package com.live.pharmaliv.model;

import java.sql.Timestamp;

/**
 * Movement model class representing a stock movement in the system.
 * Maps to the 'movement' table in the database.
 */
public class Movement {
    private int id;
    private int batchId;
    private Type type;
    private int quantity;
    private String reason;
    private int userId;
    private Timestamp createdAt;
    
    // Additional fields to store the associated batch and user (not in the database)
    private Batch batch;
    private User user;

    // Enum for movement types
    public enum Type {
        entry, exit, adjustment;
    }

    // Default constructor
    public Movement() {
    }

    // Constructor with all fields except id and createdAt
    public Movement(int batchId, Type type, int quantity, String reason, int userId) {
        this.batchId = batchId;
        this.type = type;
        this.quantity = quantity;
        this.reason = reason;
        this.userId = userId;
    }

    // Constructor with all fields
    public Movement(int id, int batchId, Type type, int quantity, String reason, int userId, Timestamp createdAt) {
        this.id = id;
        this.batchId = batchId;
        this.type = type;
        this.quantity = quantity;
        this.reason = reason;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBatchId() {
        return batchId;
    }

    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Batch getBatch() {
        return batch;
    }
    
    public void setBatch(Batch batch) {
        this.batch = batch;
        if (batch != null) {
            this.batchId = batch.getId();
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

    @Override
    public String toString() {
        return "Movement{" +
                "id=" + id +
                ", batchId=" + batchId +
                ", type=" + type +
                ", quantity=" + quantity +
                ", reason='" + reason + '\'' +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                '}';
    }
}