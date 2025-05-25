package com.live.pharmaliv.dao;

import com.live.pharmaliv.model.Alert;
import com.live.pharmaliv.model.Product;
import com.live.pharmaliv.model.Batch;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the AlertDAO interface.
 * Provides methods for CRUD operations on alerts.
 */
public class AlertDAOImpl implements AlertDAO {
    private static final Logger LOGGER = Logger.getLogger(AlertDAOImpl.class.getName());
    private ProductDAO productDAO;
    
    public AlertDAOImpl() {
        this.productDAO = new ProductDAOImpl();
    }

    @Override
    public Alert findById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Alert alert = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM alert WHERE id = ?");
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                alert = mapResultSetToAlert(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding alert by ID: " + id, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return alert;
    }

    @Override
    public List<Alert> findAll() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Alert> alerts = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM alert ORDER BY created_at DESC");

            while (rs.next()) {
                alerts.add(mapResultSetToAlert(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all alerts", e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return alerts;
    }

    @Override
    public List<Alert> findByProductId(int productId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Alert> alerts = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM alert WHERE product_id = ? ORDER BY created_at DESC");
            stmt.setInt(1, productId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                alerts.add(mapResultSetToAlert(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding alerts by product ID: " + productId, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return alerts;
    }

    @Override
    public List<Alert> findByType(Alert.Type type) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Alert> alerts = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM alert WHERE type = ? ORDER BY created_at DESC");
            stmt.setString(1, type.toString());
            rs = stmt.executeQuery();

            while (rs.next()) {
                alerts.add(mapResultSetToAlert(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding alerts by type: " + type, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return alerts;
    }

    @Override
    public List<Alert> findUnresolved() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Alert> alerts = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM alert WHERE is_resolved = FALSE ORDER BY created_at DESC");
            rs = stmt.executeQuery();

            while (rs.next()) {
                alerts.add(mapResultSetToAlert(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding unresolved alerts", e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return alerts;
    }

    @Override
    public List<Alert> findRecentAlerts(int limit) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Alert> alerts = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM alert ORDER BY created_at DESC LIMIT ?");
            stmt.setInt(1, limit);
            rs = stmt.executeQuery();

            while (rs.next()) {
                alerts.add(mapResultSetToAlert(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding recent alerts", e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return alerts;
    }

    @Override
    public int countAlertsByType(Alert.Type type) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int count = 0;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT COUNT(*) as count FROM alert WHERE type = ? AND is_resolved = FALSE");
            stmt.setString(1, type.toString());
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt("count");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting alerts by type: " + type, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return count;
    }

    @Override
    public Alert save(Alert alert) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();

            if (alert.getId() == 0) {
                // Insert new alert
                stmt = conn.prepareStatement(
                        "INSERT INTO alert (product_id, type, message, is_resolved) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                stmt.setInt(1, alert.getProductId());
                stmt.setString(2, alert.getType().toString());
                stmt.setString(3, alert.getMessage());
                stmt.setBoolean(4, alert.isResolved());

                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating alert failed, no rows affected.");
                }

                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    alert.setId(rs.getInt(1));
                } else {
                    throw new SQLException("Creating alert failed, no ID obtained.");
                }
            } else {
                // Update existing alert
                stmt = conn.prepareStatement(
                        "UPDATE alert SET product_id = ?, type = ?, message = ?, is_resolved = ?, resolved_at = ? WHERE id = ?");
                stmt.setInt(1, alert.getProductId());
                stmt.setString(2, alert.getType().toString());
                stmt.setString(3, alert.getMessage());
                stmt.setBoolean(4, alert.isResolved());
                stmt.setTimestamp(5, alert.getResolvedAt());
                stmt.setInt(6, alert.getId());

                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving alert", e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return alert;
    }

    @Override
    public boolean delete(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean deleted = false;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("DELETE FROM alert WHERE id = ?");
            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            deleted = affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting alert with ID: " + id, e);
        } finally {
            closeResources(conn, stmt, null);
        }

        return deleted;
    }

    @Override
    public boolean resolveAlert(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean resolved = false;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("UPDATE alert SET is_resolved = TRUE, resolved_at = ? WHERE id = ?");
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(2, id);

            int affectedRows = stmt.executeUpdate();
            resolved = affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error resolving alert with ID: " + id, e);
        } finally {
            closeResources(conn, stmt, null);
        }

        return resolved;
    }

    @Override
    public int createStockAlerts() {
        int alertsCreated = 0;
        List<Product> productsBelowThreshold = productDAO.findProductsBelowThreshold();
        
        for (Product product : productsBelowThreshold) {
            // Check if there's already an unresolved stock alert for this product
            boolean hasAlert = hasUnresolvedAlert(product.getId(), Alert.Type.stock);
            
            if (!hasAlert) {
                int stockQuantity = productDAO.getTotalStockQuantity(product.getId());
                Alert alert = new Alert();
                alert.setProductId(product.getId());
                alert.setType(Alert.Type.stock);
                alert.setMessage("Stock bas pour " + product.getName() + ". Quantité actuelle: " + stockQuantity + 
                                ", Seuil: " + product.getThresholdQuantity());
                alert.setResolved(false);
                
                save(alert);
                alertsCreated++;
            }
        }
        
        return alertsCreated;
    }

    @Override
    public int createExpiryAlerts(int daysThreshold) {
        int alertsCreated = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            // Find batches expiring within the threshold
            String sql = "SELECT b.*, p.id as product_id, p.name as product_name " +
                         "FROM batch b " +
                         "JOIN product p ON b.product_id = p.id " +
                         "WHERE b.expiry_date BETWEEN CURRENT_DATE AND DATE_ADD(CURRENT_DATE, INTERVAL ? DAY) " +
                         "AND b.quantity > 0";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, daysThreshold);
            rs = stmt.executeQuery();

            while (rs.next()) {
                int productId = rs.getInt("product_id");
                String productName = rs.getString("product_name");
                Date expiryDate = rs.getDate("expiry_date");
                String batchNumber = rs.getString("batch_number");
                
                // Check if there's already an unresolved expiry alert for this product
                boolean hasAlert = hasUnresolvedAlert(productId, Alert.Type.expiry);
                
                if (!hasAlert) {
                    Alert alert = new Alert();
                    alert.setProductId(productId);
                    alert.setType(Alert.Type.expiry);
                    alert.setMessage("Lot " + batchNumber + " du produit " + productName + 
                                    " expire le " + expiryDate);
                    alert.setResolved(false);
                    
                    save(alert);
                    alertsCreated++;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating expiry alerts", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return alertsCreated;
    }
    
    /**
     * Check if a product has an unresolved alert of a specific type.
     * @param productId The product ID
     * @param type The alert type
     * @return true if the product has an unresolved alert of the specified type, false otherwise
     */
    private boolean hasUnresolvedAlert(int productId, Alert.Type type) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean hasAlert = false;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT COUNT(*) as count FROM alert WHERE product_id = ? AND type = ? AND is_resolved = FALSE");
            stmt.setInt(1, productId);
            stmt.setString(2, type.toString());
            rs = stmt.executeQuery();

            if (rs.next()) {
                hasAlert = rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking for unresolved alerts", e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return hasAlert;
    }

    /**
     * Map a ResultSet to an Alert object.
     * @param rs The ResultSet
     * @return The Alert object
     * @throws SQLException If a database access error occurs
     */
    private Alert mapResultSetToAlert(ResultSet rs) throws SQLException {
        Alert alert = new Alert();
        alert.setId(rs.getInt("id"));
        alert.setProductId(rs.getInt("product_id"));
        alert.setType(Alert.Type.valueOf(rs.getString("type")));
        alert.setMessage(rs.getString("message"));
        alert.setResolved(rs.getBoolean("is_resolved"));
        alert.setCreatedAt(rs.getTimestamp("created_at"));
        alert.setResolvedAt(rs.getTimestamp("resolved_at"));
        
        // Load the associated product
        Product product = productDAO.findById(alert.getProductId());
        alert.setProduct(product);
        
        return alert;
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