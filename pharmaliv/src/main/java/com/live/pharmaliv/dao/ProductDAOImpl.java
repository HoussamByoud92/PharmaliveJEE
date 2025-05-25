package com.live.pharmaliv.dao;

import com.live.pharmaliv.model.Product;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the ProductDAO interface.
 * Provides methods for CRUD operations on products.
 */
public class ProductDAOImpl implements ProductDAO {
    private static final Logger LOGGER = Logger.getLogger(ProductDAOImpl.class.getName());

    @Override
    public Product findById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Product product = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM product WHERE id = ?");
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                product = mapResultSetToProduct(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding product by ID: " + id, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return product;
    }

    @Override
    public Product findByCode(String code) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Product product = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM product WHERE code = ?");
            stmt.setString(1, code);
            rs = stmt.executeQuery();

            if (rs.next()) {
                product = mapResultSetToProduct(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding product by code: " + code, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return product;
    }

    @Override
    public List<Product> findAll() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Product> products = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM product ORDER BY name");

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all products", e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return products;
    }

    @Override
    public List<Product> findProductsBelowThreshold() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Product> products = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            // Query to find products where total stock is below threshold
            String sql = "SELECT p.*, COALESCE(SUM(b.quantity), 0) as total_stock " +
                         "FROM product p " +
                         "LEFT JOIN batch b ON p.id = b.product_id " +
                         "GROUP BY p.id " +
                         "HAVING total_stock < p.threshold_quantity OR total_stock IS NULL";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding products below threshold", e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return products;
    }

    @Override
    public List<Product> findByName(String name) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Product> products = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM product WHERE name LIKE ? ORDER BY name");
            stmt.setString(1, "%" + name + "%");
            rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding products by name: " + name, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return products;
    }

    @Override
    public Product save(Product product) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();

            if (product.getId() == 0) {
                // Insert new product
                stmt = conn.prepareStatement(
                        "INSERT INTO product (code, name, dci, description, price, threshold_quantity) VALUES (?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, product.getCode());
                stmt.setString(2, product.getName());
                stmt.setString(3, product.getDci());
                stmt.setString(4, product.getDescription());
                stmt.setBigDecimal(5, product.getPrice());
                stmt.setInt(6, product.getThresholdQuantity());

                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating product failed, no rows affected.");
                }

                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    product.setId(rs.getInt(1));
                } else {
                    throw new SQLException("Creating product failed, no ID obtained.");
                }
            } else {
                // Update existing product
                stmt = conn.prepareStatement(
                        "UPDATE product SET code = ?, name = ?, dci = ?, description = ?, price = ?, threshold_quantity = ? WHERE id = ?");
                stmt.setString(1, product.getCode());
                stmt.setString(2, product.getName());
                stmt.setString(3, product.getDci());
                stmt.setString(4, product.getDescription());
                stmt.setBigDecimal(5, product.getPrice());
                stmt.setInt(6, product.getThresholdQuantity());
                stmt.setInt(7, product.getId());

                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving product: " + product.getName(), e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return product;
    }

    @Override
    public boolean delete(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean deleted = false;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("DELETE FROM product WHERE id = ?");
            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            deleted = affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting product with ID: " + id, e);
        } finally {
            closeResources(conn, stmt, null);
        }

        return deleted;
    }

    @Override
    public int getTotalStockQuantity(int productId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int totalQuantity = 0;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT SUM(quantity) as total FROM batch WHERE product_id = ?");
            stmt.setInt(1, productId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                totalQuantity = rs.getInt("total");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total stock quantity for product ID: " + productId, e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return totalQuantity;
    }

    @Override
    public List<Product> getBestSellingProducts(int limit) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Product> products = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            // Query to get best-selling products based on sale_item quantities
            String sql = "SELECT p.*, SUM(si.quantity) as total_sold " +
                         "FROM product p " +
                         "JOIN batch b ON p.id = b.product_id " +
                         "JOIN sale_item si ON b.id = si.batch_id " +
                         "GROUP BY p.id " +
                         "ORDER BY total_sold DESC " +
                         "LIMIT ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, limit);
            rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting best-selling products", e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return products;
    }

    /**
     * Map a ResultSet to a Product object.
     * @param rs The ResultSet
     * @return The Product object
     * @throws SQLException If a database access error occurs
     */
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setCode(rs.getString("code"));
        product.setName(rs.getString("name"));
        product.setDci(rs.getString("dci"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setThresholdQuantity(rs.getInt("threshold_quantity"));
        product.setCreatedAt(rs.getTimestamp("created_at"));
        product.setUpdatedAt(rs.getTimestamp("updated_at"));
        return product;
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