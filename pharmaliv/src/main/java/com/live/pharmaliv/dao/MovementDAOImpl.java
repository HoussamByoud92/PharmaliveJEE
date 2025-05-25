package com.live.pharmaliv.dao;

import com.live.pharmaliv.model.Movement;
import com.live.pharmaliv.model.Batch;
import com.live.pharmaliv.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the MovementDAO interface.
 * Provides methods for CRUD operations on movements.
 */
public class MovementDAOImpl implements MovementDAO {
    private static final Logger LOGGER = Logger.getLogger(MovementDAOImpl.class.getName());
    private BatchDAO batchDAO;
    private UserDAO userDAO;

    public MovementDAOImpl() {
        this.batchDAO = new BatchDAOImpl();
        this.userDAO = new UserDAOImpl();
    }

    @Override
    public Movement findById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Movement movement = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM movement WHERE id = ?");
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                movement = mapResultSetToMovement(rs);
                // Load the associated batch and user
                Batch batch = batchDAO.findById(movement.getBatchId());
                User user = userDAO.findById(movement.getUserId());
                movement.setBatch(batch);
                movement.setUser(user);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding movement by ID: " + id, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return movement;
    }

    @Override
    public List<Movement> findByBatchId(int batchId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Movement> movements = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM movement WHERE batch_id = ? ORDER BY created_at DESC");
            stmt.setInt(1, batchId);
            rs = stmt.executeQuery();

            Batch batch = batchDAO.findById(batchId);
            while (rs.next()) {
                Movement movement = mapResultSetToMovement(rs);
                movement.setBatch(batch);
                // Load the associated user
                User user = userDAO.findById(movement.getUserId());
                movement.setUser(user);
                movements.add(movement);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding movements by batch ID: " + batchId, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return movements;
    }

    @Override
    public List<Movement> findByUserId(int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Movement> movements = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM movement WHERE user_id = ? ORDER BY created_at DESC");
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            User user = userDAO.findById(userId);
            while (rs.next()) {
                Movement movement = mapResultSetToMovement(rs);
                movement.setUser(user);
                // Load the associated batch
                Batch batch = batchDAO.findById(movement.getBatchId());
                movement.setBatch(batch);
                movements.add(movement);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding movements by user ID: " + userId, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return movements;
    }

    @Override
    public List<Movement> findAll() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Movement> movements = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM movement ORDER BY created_at DESC");

            while (rs.next()) {
                Movement movement = mapResultSetToMovement(rs);
                // Load the associated batch and user
                Batch batch = batchDAO.findById(movement.getBatchId());
                User user = userDAO.findById(movement.getUserId());
                movement.setBatch(batch);
                movement.setUser(user);
                movements.add(movement);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all movements", e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return movements;
    }

    @Override
    public List<Movement> findByType(Movement.Type type) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Movement> movements = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM movement WHERE type = ? ORDER BY created_at DESC");
            stmt.setString(1, type.toString());
            rs = stmt.executeQuery();

            while (rs.next()) {
                Movement movement = mapResultSetToMovement(rs);
                // Load the associated batch and user
                Batch batch = batchDAO.findById(movement.getBatchId());
                User user = userDAO.findById(movement.getUserId());
                movement.setBatch(batch);
                movement.setUser(user);
                movements.add(movement);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding movements by type: " + type, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return movements;
    }

    @Override
    public List<Movement> findByDateRange(Date startDate, Date endDate) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Movement> movements = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM movement WHERE DATE(created_at) BETWEEN ? AND ? ORDER BY created_at DESC");
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Movement movement = mapResultSetToMovement(rs);
                // Load the associated batch and user
                Batch batch = batchDAO.findById(movement.getBatchId());
                User user = userDAO.findById(movement.getUserId());
                movement.setBatch(batch);
                movement.setUser(user);
                movements.add(movement);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding movements by date range", e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return movements;
    }

    @Override
    public Movement save(Movement movement) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            DatabaseUtil.beginTransaction(conn);

            if (movement.getId() == 0) {
                // Insert new movement
                stmt = conn.prepareStatement(
                        "INSERT INTO movement (batch_id, type, quantity, reason, user_id) VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                stmt.setInt(1, movement.getBatchId());
                stmt.setString(2, movement.getType().toString());
                stmt.setInt(3, movement.getQuantity());
                stmt.setString(4, movement.getReason());
                stmt.setInt(5, movement.getUserId());

                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating movement failed, no rows affected.");
                }

                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    movement.setId(rs.getInt(1));
                } else {
                    throw new SQLException("Creating movement failed, no ID obtained.");
                }

                // Update batch quantity based on movement type
                Batch batch = batchDAO.findById(movement.getBatchId());
                int newQuantity = batch.getQuantity();
                
                switch (movement.getType()) {
                    case entry:
                        newQuantity += movement.getQuantity();
                        break;
                    case exit:
                        newQuantity -= movement.getQuantity();
                        break;
                    case adjustment:
                        newQuantity += movement.getQuantity(); // Adjustment can be positive or negative
                        break;
                }
                
                // Update batch quantity
                batchDAO.updateQuantity(batch.getId(), newQuantity);
            } else {
                // Update existing movement is not allowed as it would affect inventory
                throw new UnsupportedOperationException("Updating existing movements is not supported as it would affect inventory history.");
            }

            DatabaseUtil.commitTransaction(conn);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving movement", e);
            DatabaseUtil.rollbackTransaction(conn);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return movement;
    }

    @Override
    public boolean delete(int id) {
        // Deleting movements is not allowed as it would affect inventory history
        throw new UnsupportedOperationException("Deleting movements is not supported as it would affect inventory history.");
    }

    @Override
    public int getTotalEntriesForBatch(int batchId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int totalEntries = 0;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT SUM(quantity) as total FROM movement WHERE batch_id = ? AND type = 'entry'");
            stmt.setInt(1, batchId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                totalEntries = rs.getInt("total");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total entries for batch ID: " + batchId, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return totalEntries;
    }

    @Override
    public int getTotalExitsForBatch(int batchId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int totalExits = 0;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT SUM(quantity) as total FROM movement WHERE batch_id = ? AND type = 'exit'");
            stmt.setInt(1, batchId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                totalExits = rs.getInt("total");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total exits for batch ID: " + batchId, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return totalExits;
    }

    @Override
    public int getTotalAdjustmentsForBatch(int batchId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int totalAdjustments = 0;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT SUM(quantity) as total FROM movement WHERE batch_id = ? AND type = 'adjustment'");
            stmt.setInt(1, batchId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                totalAdjustments = rs.getInt("total");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total adjustments for batch ID: " + batchId, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return totalAdjustments;
    }

    /**
     * Map a ResultSet to a Movement object.
     * @param rs The ResultSet
     * @return The Movement object
     * @throws SQLException If a database access error occurs
     */
    private Movement mapResultSetToMovement(ResultSet rs) throws SQLException {
        Movement movement = new Movement();
        movement.setId(rs.getInt("id"));
        movement.setBatchId(rs.getInt("batch_id"));
        movement.setType(Movement.Type.valueOf(rs.getString("type")));
        movement.setQuantity(rs.getInt("quantity"));
        movement.setReason(rs.getString("reason"));
        movement.setUserId(rs.getInt("user_id"));
        movement.setCreatedAt(rs.getTimestamp("created_at"));
        return movement;
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