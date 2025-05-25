package com.live.pharmaliv.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Product model class representing a product in the system.
 * Maps to the 'product' table in the database.
 */
public class Product {
    private int id;
    private String code;
    private String name;
    private String dci;
    private String description;
    private BigDecimal price;
    private int thresholdQuantity;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Default constructor
    public Product() {
    }

    // Constructor with all fields except id, createdAt, and updatedAt
    public Product(String code, String name, String dci, String description, BigDecimal price, int thresholdQuantity) {
        this.code = code;
        this.name = name;
        this.dci = dci;
        this.description = description;
        this.price = price;
        this.thresholdQuantity = thresholdQuantity;
    }

    // Constructor with all fields
    public Product(int id, String code, String name, String dci, String description, BigDecimal price, int thresholdQuantity, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.dci = dci;
        this.description = description;
        this.price = price;
        this.thresholdQuantity = thresholdQuantity;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDci() {
        return dci;
    }

    public void setDci(String dci) {
        this.dci = dci;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getThresholdQuantity() {
        return thresholdQuantity;
    }

    public void setThresholdQuantity(int thresholdQuantity) {
        this.thresholdQuantity = thresholdQuantity;
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

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", dci='" + dci + '\'' +
                ", price=" + price +
                ", thresholdQuantity=" + thresholdQuantity +
                '}';
    }
}