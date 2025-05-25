package com.live.pharmaliv.dao;

import com.live.pharmaliv.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the CustomerDAO interface.
 * Provides methods for CRUD operations on customers.
 */
public class CustomerDAOImpl implements CustomerDAO {
    private static final Logger LOGGER = Logger.getLogger(CustomerDAOImpl.class.getName());

    @Override
    public Customer findById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Customer customer = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM customer WHERE id = ?");
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                customer = mapResultSetToCustomer(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding customer by ID: " + id, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return customer;
    }

    @Override
    public List<Customer> findAll() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Customer> customers = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM customer ORDER BY name");

            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all customers", e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return customers;
    }

    @Override
    public List<Customer> findByName(String name) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Customer> customers = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM customer WHERE name LIKE ? ORDER BY name");
            stmt.setString(1, "%" + name + "%");
            rs = stmt.executeQuery();

            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding customers by name: " + name, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return customers;
    }

    @Override
    public Customer findByEmail(String email) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Customer customer = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM customer WHERE email = ?");
            stmt.setString(1, email);
            rs = stmt.executeQuery();

            if (rs.next()) {
                customer = mapResultSetToCustomer(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding customer by email: " + email, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return customer;
    }

    @Override
    public Customer findByPhone(String phone) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Customer customer = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM customer WHERE phone = ?");
            stmt.setString(1, phone);
            rs = stmt.executeQuery();

            if (rs.next()) {
                customer = mapResultSetToCustomer(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding customer by phone: " + phone, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return customer;
    }

    @Override
    public Customer save(Customer customer) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();

            if (customer.getId() == 0) {
                // Insert new customer
                stmt = conn.prepareStatement(
                        "INSERT INTO customer (name, phone, email, address) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, customer.getName());
                stmt.setString(2, customer.getPhone());
                stmt.setString(3, customer.getEmail());
                stmt.setString(4, customer.getAddress());

                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating customer failed, no rows affected.");
                }

                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    customer.setId(rs.getInt(1));
                } else {
                    throw new SQLException("Creating customer failed, no ID obtained.");
                }
            } else {
                // Update existing customer
                stmt = conn.prepareStatement(
                        "UPDATE customer SET name = ?, phone = ?, email = ?, address = ? WHERE id = ?");
                stmt.setString(1, customer.getName());
                stmt.setString(2, customer.getPhone());
                stmt.setString(3, customer.getEmail());
                stmt.setString(4, customer.getAddress());
                stmt.setInt(5, customer.getId());

                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving customer: " + customer.getName(), e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return customer;
    }

    @Override
    public boolean delete(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean deleted = false;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("DELETE FROM customer WHERE id = ?");
            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            deleted = affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting customer with ID: " + id, e);
        } finally {
            closeResources(conn, stmt, null);
        }

        return deleted;
    }

    /**
     * Map a ResultSet to a Customer object.
     * @param rs The ResultSet
     * @return The Customer object
     * @throws SQLException If a database access error occurs
     */
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getInt("id"));
        customer.setName(rs.getString("name"));
        customer.setPhone(rs.getString("phone"));
        customer.setEmail(rs.getString("email"));
        customer.setAddress(rs.getString("address"));
        customer.setCreatedAt(rs.getTimestamp("created_at"));
        customer.setUpdatedAt(rs.getTimestamp("updated_at"));
        return customer;
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