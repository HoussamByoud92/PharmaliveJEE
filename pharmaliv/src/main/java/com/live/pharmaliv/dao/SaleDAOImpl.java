package com.live.pharmaliv.dao;

import com.live.pharmaliv.model.Sale;
import com.live.pharmaliv.model.SaleItem;
import com.live.pharmaliv.model.Customer;
import com.live.pharmaliv.model.User;
import com.live.pharmaliv.model.Batch;
import com.live.pharmaliv.model.Product;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the SaleDAO interface.
 * Provides methods for CRUD operations on sales.
 */
public class SaleDAOImpl implements SaleDAO {
    private static final Logger LOGGER = Logger.getLogger(SaleDAOImpl.class.getName());
    private UserDAO userDAO;
    private ProductDAO productDAO;
    
    public SaleDAOImpl() {
        this.userDAO = new UserDAOImpl();
        this.productDAO = new ProductDAOImpl();
    }

    @Override
    public Sale findById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Sale sale = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM sale WHERE id = ?");
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                sale = mapResultSetToSale(rs);
                
                // Load sale items
                List<SaleItem> saleItems = findSaleItemsBySaleId(sale.getId());
                sale.setSaleItems(saleItems);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding sale by ID: " + id, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return sale;
    }

    @Override
    public List<Sale> findAll() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Sale> sales = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM sale ORDER BY created_at DESC");

            while (rs.next()) {
                Sale sale = mapResultSetToSale(rs);
                
                // Load sale items
                List<SaleItem> saleItems = findSaleItemsBySaleId(sale.getId());
                sale.setSaleItems(saleItems);
                
                sales.add(sale);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all sales", e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return sales;
    }

    @Override
    public List<Sale> findByCustomerId(int customerId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Sale> sales = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM sale WHERE customer_id = ? ORDER BY created_at DESC");
            stmt.setInt(1, customerId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Sale sale = mapResultSetToSale(rs);
                
                // Load sale items
                List<SaleItem> saleItems = findSaleItemsBySaleId(sale.getId());
                sale.setSaleItems(saleItems);
                
                sales.add(sale);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding sales by customer ID: " + customerId, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return sales;
    }

    @Override
    public List<Sale> findByUserId(int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Sale> sales = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM sale WHERE user_id = ? ORDER BY created_at DESC");
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Sale sale = mapResultSetToSale(rs);
                
                // Load sale items
                List<SaleItem> saleItems = findSaleItemsBySaleId(sale.getId());
                sale.setSaleItems(saleItems);
                
                sales.add(sale);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding sales by user ID: " + userId, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return sales;
    }

    @Override
    public List<Sale> findByDate(LocalDate date) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Sale> sales = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM sale WHERE DATE(created_at) = ? ORDER BY created_at DESC");
            stmt.setDate(1, Date.valueOf(date));
            rs = stmt.executeQuery();

            while (rs.next()) {
                Sale sale = mapResultSetToSale(rs);
                
                // Load sale items
                List<SaleItem> saleItems = findSaleItemsBySaleId(sale.getId());
                sale.setSaleItems(saleItems);
                
                sales.add(sale);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding sales by date: " + date, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return sales;
    }

    @Override
    public List<Sale> findBetweenDates(LocalDate startDate, LocalDate endDate) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Sale> sales = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM sale WHERE DATE(created_at) BETWEEN ? AND ? ORDER BY created_at DESC");
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            rs = stmt.executeQuery();

            while (rs.next()) {
                Sale sale = mapResultSetToSale(rs);
                
                // Load sale items
                List<SaleItem> saleItems = findSaleItemsBySaleId(sale.getId());
                sale.setSaleItems(saleItems);
                
                sales.add(sale);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding sales between dates: " + startDate + " and " + endDate, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return sales;
    }

    @Override
    public BigDecimal getTotalSalesForDate(LocalDate date) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        BigDecimal total = BigDecimal.ZERO;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT SUM(total_amount) as total FROM sale WHERE DATE(created_at) = ?");
            stmt.setDate(1, Date.valueOf(date));
            rs = stmt.executeQuery();

            if (rs.next()) {
                BigDecimal result = rs.getBigDecimal("total");
                if (result != null) {
                    total = result;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total sales for date: " + date, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return total;
    }

    @Override
    public BigDecimal getTotalSalesBetweenDates(LocalDate startDate, LocalDate endDate) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        BigDecimal total = BigDecimal.ZERO;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT SUM(total_amount) as total FROM sale WHERE DATE(created_at) BETWEEN ? AND ?");
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            rs = stmt.executeQuery();

            if (rs.next()) {
                BigDecimal result = rs.getBigDecimal("total");
                if (result != null) {
                    total = result;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total sales between dates: " + startDate + " and " + endDate, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return total;
    }

    @Override
    public Sale save(Sale sale) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            DatabaseUtil.beginTransaction(conn);

            if (sale.getId() == 0) {
                // Insert new sale
                stmt = conn.prepareStatement(
                        "INSERT INTO sale (customer_id, user_id, total_amount) VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                
                if (sale.getCustomerId() != null) {
                    stmt.setInt(1, sale.getCustomerId());
                } else {
                    stmt.setNull(1, Types.INTEGER);
                }
                
                stmt.setInt(2, sale.getUserId());
                stmt.setBigDecimal(3, sale.getTotalAmount());

                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating sale failed, no rows affected.");
                }

                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    sale.setId(rs.getInt(1));
                } else {
                    throw new SQLException("Creating sale failed, no ID obtained.");
                }
                
                // Save sale items
                for (SaleItem item : sale.getSaleItems()) {
                    item.setSaleId(sale.getId());
                    saveSaleItem(item);
                    
                    // Update batch quantity
                    updateBatchQuantity(item.getBatchId(), -item.getQuantity());
                }
            } else {
                // Update existing sale
                stmt = conn.prepareStatement(
                        "UPDATE sale SET customer_id = ?, user_id = ?, total_amount = ? WHERE id = ?");
                
                if (sale.getCustomerId() != null) {
                    stmt.setInt(1, sale.getCustomerId());
                } else {
                    stmt.setNull(1, Types.INTEGER);
                }
                
                stmt.setInt(2, sale.getUserId());
                stmt.setBigDecimal(3, sale.getTotalAmount());
                stmt.setInt(4, sale.getId());

                stmt.executeUpdate();
                
                // Update sale items
                // First, get existing sale items
                List<SaleItem> existingItems = findSaleItemsBySaleId(sale.getId());
                
                // Then, update or create new items
                for (SaleItem item : sale.getSaleItems()) {
                    item.setSaleId(sale.getId());
                    saveSaleItem(item);
                }
                
                // Finally, delete items that are no longer in the sale
                for (SaleItem existingItem : existingItems) {
                    boolean found = false;
                    for (SaleItem item : sale.getSaleItems()) {
                        if (existingItem.getId() == item.getId()) {
                            found = true;
                            break;
                        }
                    }
                    
                    if (!found) {
                        // Restore batch quantity before deleting
                        updateBatchQuantity(existingItem.getBatchId(), existingItem.getQuantity());
                        deleteSaleItem(existingItem.getId());
                    }
                }
            }
            
            DatabaseUtil.commitTransaction(conn);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving sale", e);
            DatabaseUtil.rollbackTransaction(conn);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return sale;
    }

    @Override
    public boolean delete(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean deleted = false;

        try {
            conn = DatabaseUtil.getConnection();
            DatabaseUtil.beginTransaction(conn);
            
            // First, restore batch quantities for all sale items
            List<SaleItem> saleItems = findSaleItemsBySaleId(id);
            for (SaleItem item : saleItems) {
                updateBatchQuantity(item.getBatchId(), item.getQuantity());
            }
            
            // Then, delete all sale items
            stmt = conn.prepareStatement("DELETE FROM sale_item WHERE sale_id = ?");
            stmt.setInt(1, id);
            stmt.executeUpdate();
            
            // Finally, delete the sale
            stmt = conn.prepareStatement("DELETE FROM sale WHERE id = ?");
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            deleted = affectedRows > 0;
            
            DatabaseUtil.commitTransaction(conn);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting sale with ID: " + id, e);
            DatabaseUtil.rollbackTransaction(conn);
        } finally {
            closeResources(conn, stmt, null);
        }

        return deleted;
    }

    @Override
    public SaleItem findSaleItemById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        SaleItem saleItem = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM sale_item WHERE id = ?");
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                saleItem = mapResultSetToSaleItem(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding sale item by ID: " + id, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return saleItem;
    }

    @Override
    public List<SaleItem> findSaleItemsBySaleId(int saleId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<SaleItem> saleItems = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM sale_item WHERE sale_id = ?");
            stmt.setInt(1, saleId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                saleItems.add(mapResultSetToSaleItem(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding sale items by sale ID: " + saleId, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return saleItems;
    }

    @Override
    public SaleItem saveSaleItem(SaleItem saleItem) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();

            if (saleItem.getId() == 0) {
                // Insert new sale item
                stmt = conn.prepareStatement(
                        "INSERT INTO sale_item (sale_id, batch_id, quantity, unit_price, total_price) VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                stmt.setInt(1, saleItem.getSaleId());
                stmt.setInt(2, saleItem.getBatchId());
                stmt.setInt(3, saleItem.getQuantity());
                stmt.setBigDecimal(4, saleItem.getUnitPrice());
                stmt.setBigDecimal(5, saleItem.getTotalPrice());

                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating sale item failed, no rows affected.");
                }

                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    saleItem.setId(rs.getInt(1));
                } else {
                    throw new SQLException("Creating sale item failed, no ID obtained.");
                }
                
                // Update batch quantity
                updateBatchQuantity(saleItem.getBatchId(), -saleItem.getQuantity());
            } else {
                // Get the existing sale item to calculate quantity difference
                SaleItem existingItem = findSaleItemById(saleItem.getId());
                int quantityDifference = existingItem.getQuantity() - saleItem.getQuantity();
                
                // Update existing sale item
                stmt = conn.prepareStatement(
                        "UPDATE sale_item SET sale_id = ?, batch_id = ?, quantity = ?, unit_price = ?, total_price = ? WHERE id = ?");
                stmt.setInt(1, saleItem.getSaleId());
                stmt.setInt(2, saleItem.getBatchId());
                stmt.setInt(3, saleItem.getQuantity());
                stmt.setBigDecimal(4, saleItem.getUnitPrice());
                stmt.setBigDecimal(5, saleItem.getTotalPrice());
                stmt.setInt(6, saleItem.getId());

                stmt.executeUpdate();
                
                // Update batch quantity if needed
                if (quantityDifference != 0) {
                    updateBatchQuantity(saleItem.getBatchId(), quantityDifference);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving sale item", e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return saleItem;
    }

    @Override
    public boolean deleteSaleItem(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean deleted = false;

        try {
            conn = DatabaseUtil.getConnection();
            
            // First, get the sale item to restore batch quantity
            SaleItem saleItem = findSaleItemById(id);
            if (saleItem != null) {
                // Restore batch quantity
                updateBatchQuantity(saleItem.getBatchId(), saleItem.getQuantity());
                
                // Delete the sale item
                stmt = conn.prepareStatement("DELETE FROM sale_item WHERE id = ?");
                stmt.setInt(1, id);
                int affectedRows = stmt.executeUpdate();
                deleted = affectedRows > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting sale item with ID: " + id, e);
        } finally {
            closeResources(conn, stmt, null);
        }

        return deleted;
    }
    
    /**
     * Update the quantity of a batch.
     * @param batchId The batch ID
     * @param quantityChange The quantity change (positive for increase, negative for decrease)
     * @throws SQLException If a database access error occurs
     */
    private void updateBatchQuantity(int batchId, int quantityChange) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("UPDATE batch SET quantity = quantity + ? WHERE id = ?");
            stmt.setInt(1, quantityChange);
            stmt.setInt(2, batchId);
            stmt.executeUpdate();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            // Don't close the connection here, it might be part of a transaction
        }
    }

    /**
     * Map a ResultSet to a Sale object.
     * @param rs The ResultSet
     * @return The Sale object
     * @throws SQLException If a database access error occurs
     */
    private Sale mapResultSetToSale(ResultSet rs) throws SQLException {
        Sale sale = new Sale();
        sale.setId(rs.getInt("id"));
        
        // Handle nullable customer_id
        int customerId = rs.getInt("customer_id");
        if (!rs.wasNull()) {
            sale.setCustomerId(customerId);
            
            // Load the associated customer
            CustomerDAO customerDAO = new CustomerDAOImpl();
            Customer customer = customerDAO.findById(customerId);
            sale.setCustomer(customer);
        }
        
        sale.setUserId(rs.getInt("user_id"));
        sale.setTotalAmount(rs.getBigDecimal("total_amount"));
        sale.setCreatedAt(rs.getTimestamp("created_at"));
        
        // Load the associated user
        User user = userDAO.findById(sale.getUserId());
        sale.setUser(user);
        
        return sale;
    }

    /**
     * Map a ResultSet to a SaleItem object.
     * @param rs The ResultSet
     * @return The SaleItem object
     * @throws SQLException If a database access error occurs
     */
    private SaleItem mapResultSetToSaleItem(ResultSet rs) throws SQLException {
        SaleItem saleItem = new SaleItem();
        saleItem.setId(rs.getInt("id"));
        saleItem.setSaleId(rs.getInt("sale_id"));
        saleItem.setBatchId(rs.getInt("batch_id"));
        saleItem.setQuantity(rs.getInt("quantity"));
        saleItem.setUnitPrice(rs.getBigDecimal("unit_price"));
        saleItem.setTotalPrice(rs.getBigDecimal("total_price"));
        
        // Load the associated batch
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet batchRs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT b.*, p.* FROM batch b JOIN product p ON b.product_id = p.id WHERE b.id = ?");
            stmt.setInt(1, saleItem.getBatchId());
            batchRs = stmt.executeQuery();
            
            if (batchRs.next()) {
                Batch batch = new Batch();
                batch.setId(batchRs.getInt("b.id"));
                batch.setProductId(batchRs.getInt("b.product_id"));
                batch.setBatchNumber(batchRs.getString("b.batch_number"));
                batch.setQuantity(batchRs.getInt("b.quantity"));
                batch.setExpiryDate(batchRs.getDate("b.expiry_date"));
                batch.setPurchasePrice(batchRs.getBigDecimal("b.purchase_price"));
                batch.setCreatedAt(batchRs.getTimestamp("b.created_at"));
                batch.setUpdatedAt(batchRs.getTimestamp("b.updated_at"));
                
                // Load the associated product
                Product product = new Product();
                product.setId(batchRs.getInt("p.id"));
                product.setCode(batchRs.getString("p.code"));
                product.setName(batchRs.getString("p.name"));
                product.setDci(batchRs.getString("p.dci"));
                product.setDescription(batchRs.getString("p.description"));
                product.setPrice(batchRs.getBigDecimal("p.price"));
                product.setThresholdQuantity(batchRs.getInt("p.threshold_quantity"));
                product.setCreatedAt(batchRs.getTimestamp("p.created_at"));
                product.setUpdatedAt(batchRs.getTimestamp("p.updated_at"));
                
                batch.setProduct(product);
                saleItem.setBatch(batch);
            }
        } finally {
            if (batchRs != null) {
                batchRs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            // Don't close the connection here, it might be part of a transaction
        }
        
        return saleItem;
    }

    /**
     * Close database resources.
     * @param conn The Connection
     * @param stmt The Statement
     * @param rs The ResultSet
     */
    private void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing ResultSet", e);
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing Statement", e);
            }
        }
        DatabaseUtil.closeConnection(conn);
    }
}