-- PHARMALIVE Database Schema

-- Drop database if exists and create a new one
DROP DATABASE IF EXISTS pharmalive;
CREATE DATABASE pharmalive CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pharmalive;

-- User table for authentication and authorization
CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- Hashed password
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role ENUM('admin', 'pharmacist', 'seller') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Product table
CREATE TABLE product (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    dci VARCHAR(100), -- Active ingredient
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    threshold_quantity INT NOT NULL, -- Alert threshold
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Batch table (lots)
CREATE TABLE batch (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    batch_number VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    expiry_date DATE NOT NULL,
    purchase_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    UNIQUE KEY (product_id, batch_number)
);

-- Movement table (entries, exits, adjustments)
CREATE TABLE movement (
    id INT AUTO_INCREMENT PRIMARY KEY,
    batch_id INT NOT NULL,
    type ENUM('entry', 'exit', 'adjustment') NOT NULL,
    quantity INT NOT NULL,
    reason TEXT,
    user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (batch_id) REFERENCES batch(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- Alert table
CREATE TABLE alert (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    type ENUM('stock', 'expiry') NOT NULL,
    message TEXT NOT NULL,
    is_resolved BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

-- Customer table
CREATE TABLE customer (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Sale table
CREATE TABLE sale (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    user_id INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- Sale item table
CREATE TABLE sale_item (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT NOT NULL,
    batch_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (sale_id) REFERENCES sale(id) ON DELETE CASCADE,
    FOREIGN KEY (batch_id) REFERENCES batch(id) ON DELETE CASCADE
);

-- Insert default admin user (password: admin123)
INSERT INTO user (username, password, full_name, email, role)
VALUES ('admin', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'Administrator', 'admin@pharmalive.com', 'admin');

-- Insert sample data for testing
INSERT INTO product (code, name, dci, description, price, threshold_quantity)
VALUES 
('P001', 'Paracetamol 500mg', 'Paracetamol', 'Pain reliever and fever reducer', 5.99, 20),
('P002', 'Amoxicillin 250mg', 'Amoxicillin', 'Antibiotic', 12.50, 15),
('P003', 'Ibuprofen 400mg', 'Ibuprofen', 'Anti-inflammatory', 7.25, 25);

-- Insert sample batches
INSERT INTO batch (product_id, batch_number, quantity, expiry_date, purchase_price)
VALUES 
(1, 'B001', 50, DATE_ADD(CURRENT_DATE, INTERVAL 1 YEAR), 3.50),
(1, 'B002', 30, DATE_ADD(CURRENT_DATE, INTERVAL 6 MONTH), 3.60),
(2, 'B003', 40, DATE_ADD(CURRENT_DATE, INTERVAL 2 YEAR), 8.00),
(3, 'B004', 60, DATE_ADD(CURRENT_DATE, INTERVAL 18 MONTH), 4.50);

-- Insert sample customers
INSERT INTO customer (name, phone, email, address)
VALUES 
('John Doe', '123-456-7890', 'john@example.com', '123 Main St'),
('Jane Smith', '987-654-3210', 'jane@example.com', '456 Oak Ave');