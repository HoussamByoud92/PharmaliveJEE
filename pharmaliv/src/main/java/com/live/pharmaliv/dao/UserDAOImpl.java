package com.live.pharmaliv.dao;

import com.live.pharmaliv.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the UserDAO interface.
 * Provides methods for CRUD operations on users.
 */
public class UserDAOImpl implements UserDAO {
    private static final Logger LOGGER = Logger.getLogger(UserDAOImpl.class.getName());

    @Override
    public User findById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM user WHERE id = ?");
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                user = mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by ID: " + id, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return user;
    }

    @Override
    public User findByUsername(String username) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM user WHERE username = ?");
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            if (rs.next()) {
                user = mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by username: " + username, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return user;
    }

    @Override
    public List<User> findAll() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM user ORDER BY username");

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all users", e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return users;
    }

    @Override
    public User save(User user) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();

            if (user.getId() == 0) {
                // Insert new user
                stmt = conn.prepareStatement(
                        "INSERT INTO user (username, password, full_name, email, role) VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, user.getUsername());

                // Hash the password if it's not already hashed
                if (!user.getPassword().startsWith("$2a$")) {
                    stmt.setString(2, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
                } else {
                    stmt.setString(2, user.getPassword());
                }

                stmt.setString(3, user.getFullName());
                stmt.setString(4, user.getEmail());
                stmt.setString(5, user.getRole().toString());

                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating user failed, no rows affected.");
                }

                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            } else {
                // Update existing user
                if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                    // Update with password
                    stmt = conn.prepareStatement(
                            "UPDATE user SET username = ?, password = ?, full_name = ?, email = ?, role = ? WHERE id = ?");

                    // Hash the password if it's not already hashed
                    if (!user.getPassword().startsWith("$2a$")) {
                        stmt.setString(2, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
                    } else {
                        stmt.setString(2, user.getPassword());
                    }
                } else {
                    // Update without password
                    stmt = conn.prepareStatement(
                            "UPDATE user SET username = ?, full_name = ?, email = ?, role = ? WHERE id = ?");
                }

                stmt.setString(1, user.getUsername());

                if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                    // When updating with password, parameters start at index 3
                    stmt.setString(3, user.getFullName());
                    stmt.setString(4, user.getEmail());
                    stmt.setString(5, user.getRole().toString());
                    stmt.setInt(6, user.getId());
                } else {
                    // When updating without password, parameters start at index 2
                    stmt.setString(2, user.getFullName());
                    stmt.setString(3, user.getEmail());
                    stmt.setString(4, user.getRole().toString());
                    stmt.setInt(5, user.getId());
                }

                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving user: " + user.getUsername(), e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return user;
    }

    @Override
    public boolean delete(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean deleted = false;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("DELETE FROM user WHERE id = ?");
            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            deleted = affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting user with ID: " + id, e);
        } finally {
            closeResources(conn, stmt, null);
        }

        return deleted;
    }

    @Override
    public User authenticate(String username, String password) {
        User user = findByUsername(username);

        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            return user;
        }

        return null;
    }

    /**
     * Map a ResultSet to a User object.
     * @param rs The ResultSet
     * @return The User object
     * @throws SQLException If a database access error occurs
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setRole(User.Role.valueOf(rs.getString("role")));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        return user;
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
