package com.live.pharmaliv.dao;

import com.live.pharmaliv.model.Batch;
import com.live.pharmaliv.model.Product;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the BatchDAO interface.
 * Provides methods for CRUD operations on batches.
 */
public class BatchDAOImpl implements BatchDAO {
    private static final Logger LOGGER = Logger.getLogger(BatchDAOImpl.class.getName());
    private ProductDAO productDAO;

    public BatchDAOImpl() {
        this.productDAO = new ProductDAOImpl();
    }

    @Override
    public Batch findById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Batch batch = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM batch WHERE id = ?");
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                batch = mapResultSetToBatch(rs);
                // Load the associated product
                Product product = productDAO.findById(batch.getProductId());
                batch.setProduct(product);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding batch by ID: " + id, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return batch;
    }

    @Override
    public List<Batch> findByProductId(int productId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Batch> batches = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM batch WHERE product_id = ? ORDER BY expiry_date");
            stmt.setInt(1, productId);
            rs = stmt.executeQuery();

            Product product = productDAO.findById(productId);
            while (rs.next()) {
                Batch batch = mapResultSetToBatch(rs);
                batch.setProduct(product);
                batches.add(batch);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding batches by product ID: " + productId, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return batches;
    }

    @Override
    public List<Batch> findAll() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Batch> batches = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM batch ORDER BY expiry_date");

            while (rs.next()) {
                Batch batch = mapResultSetToBatch(rs);
                // Load the associated product
                Product product = productDAO.findById(batch.getProductId());
                batch.setProduct(product);
                batches.add(batch);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all batches", e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return batches;
    }

    @Override
    public List<Batch> findBatchesExpiringSoon() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Batch> batches = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            // Find batches expiring in the next 30 days
            String sql = "SELECT * FROM batch WHERE expiry_date BETWEEN CURRENT_DATE AND DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY) ORDER BY expiry_date";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Batch batch = mapResultSetToBatch(rs);
                // Load the associated product
                Product product = productDAO.findById(batch.getProductId());
                batch.setProduct(product);
                batches.add(batch);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding batches expiring soon", e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return batches;
    }

    @Override
    public List<Batch> findExpiredBatches() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Batch> batches = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            // Find batches that are already expired
            String sql = "SELECT * FROM batch WHERE expiry_date < CURRENT_DATE ORDER BY expiry_date";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Batch batch = mapResultSetToBatch(rs);
                // Load the associated product
                Product product = productDAO.findById(batch.getProductId());
                batch.setProduct(product);
                batches.add(batch);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding expired batches", e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return batches;
    }

    @Override
    public Batch save(Batch batch) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();

            if (batch.getId() == 0) {
                // Insert new batch
                stmt = conn.prepareStatement(
                        "INSERT INTO batch (product_id, batch_number, quantity, expiry_date, purchase_price) VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                stmt.setInt(1, batch.getProductId());
                stmt.setString(2, batch.getBatchNumber());
                stmt.setInt(3, batch.getQuantity());
                stmt.setDate(4, batch.getExpiryDate());
                stmt.setBigDecimal(5, batch.getPurchasePrice());

                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating batch failed, no rows affected.");
                }

                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    batch.setId(rs.getInt(1));
                } else {
                    throw new SQLException("Creating batch failed, no ID obtained.");
                }
            } else {
                // Update existing batch
                stmt = conn.prepareStatement(
                        "UPDATE batch SET product_id = ?, batch_number = ?, quantity = ?, expiry_date = ?, purchase_price = ? WHERE id = ?");
                stmt.setInt(1, batch.getProductId());
                stmt.setString(2, batch.getBatchNumber());
                stmt.setInt(3, batch.getQuantity());
                stmt.setDate(4, batch.getExpiryDate());
                stmt.setBigDecimal(5, batch.getPurchasePrice());
                stmt.setInt(6, batch.getId());

                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving batch: " + batch.getBatchNumber(), e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return batch;
    }

    @Override
    public boolean delete(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean deleted = false;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("DELETE FROM batch WHERE id = ?");
            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            deleted = affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting batch with ID: " + id, e);
        } finally {
            closeResources(conn, stmt, null);
        }

        return deleted;
    }

    @Override
    public boolean updateQuantity(int id, int quantity) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean updated = false;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("UPDATE batch SET quantity = ? WHERE id = ?");
            stmt.setInt(1, quantity);
            stmt.setInt(2, id);

            int affectedRows = stmt.executeUpdate();
            updated = affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating quantity for batch with ID: " + id, e);
        } finally {
            closeResources(conn, stmt, null);
        }

        return updated;
    }

    @Override
    public List<Batch> findByExpiryDateRange(Date startDate, Date endDate) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Batch> batches = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM batch WHERE expiry_date BETWEEN ? AND ? ORDER BY expiry_date");
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Batch batch = mapResultSetToBatch(rs);
                // Load the associated product
                Product product = productDAO.findById(batch.getProductId());
                batch.setProduct(product);
                batches.add(batch);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding batches by expiry date range", e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return batches;
    }

    /**
     * Map a ResultSet to a Batch object.
     * @param rs The ResultSet
     * @return The Batch object
     * @throws SQLException If a database access error occurs
     */
    private Batch mapResultSetToBatch(ResultSet rs) throws SQLException {
        Batch batch = new Batch();
        batch.setId(rs.getInt("id"));
        batch.setProductId(rs.getInt("product_id"));
        batch.setBatchNumber(rs.getString("batch_number"));
        batch.setQuantity(rs.getInt("quantity"));
        batch.setExpiryDate(rs.getDate("expiry_date"));
        batch.setPurchasePrice(rs.getBigDecimal("purchase_price"));
        batch.setCreatedAt(rs.getTimestamp("created_at"));
        batch.setUpdatedAt(rs.getTimestamp("updated_at"));
        return batch;
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