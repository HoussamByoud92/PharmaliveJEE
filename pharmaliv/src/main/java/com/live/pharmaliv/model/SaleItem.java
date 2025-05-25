package com.live.pharmaliv.model;

import java.math.BigDecimal;

/**
 * SaleItem model class representing an item in a sale.
 * Maps to the 'sale_item' table in the database.
 */
public class SaleItem {
    private int id;
    private int saleId;
    private int batchId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    
    // Additional fields to store the associated sale and batch (not in the database)
    private Sale sale;
    private Batch batch;

    // Default constructor
    public SaleItem() {
    }

    // Constructor with all fields except id
    public SaleItem(int saleId, int batchId, int quantity, BigDecimal unitPrice, BigDecimal totalPrice) {
        this.saleId = saleId;
        this.batchId = batchId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    // Constructor with all fields
    public SaleItem(int id, int saleId, int batchId, int quantity, BigDecimal unitPrice, BigDecimal totalPrice) {
        this.id = id;
        this.saleId = saleId;
        this.batchId = batchId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public int getBatchId() {
        return batchId;
    }

    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        // Recalculate total price when quantity changes
        if (unitPrice != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        // Recalculate total price when unit price changes
        if (unitPrice != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public Sale getSale() {
        return sale;
    }
    
    public void setSale(Sale sale) {
        this.sale = sale;
        if (sale != null) {
            this.saleId = sale.getId();
        }
    }
    
    public Batch getBatch() {
        return batch;
    }
    
    public void setBatch(Batch batch) {
        this.batch = batch;
        if (batch != null) {
            this.batchId = batch.getId();
            // Set unit price from the product if not already set
            if (this.unitPrice == null && batch.getProduct() != null) {
                this.unitPrice = batch.getProduct().getPrice();
                // Recalculate total price
                if (this.quantity > 0) {
                    this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
                }
            }
        }
    }

    @Override
    public String toString() {
        return "SaleItem{" +
                "id=" + id +
                ", saleId=" + saleId +
                ", batchId=" + batchId +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                '}';
    }
}