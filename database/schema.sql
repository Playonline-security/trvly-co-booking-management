-- Database Schema for trvly.co
-- Travel Agency Management System

-- Create database (run this manually if needed)
-- CREATE DATABASE trvly_co;

-- Roles table
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

-- Permissions table
CREATE TABLE IF NOT EXISTS permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- Role-Permission mapping
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User-Role mapping
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Clients table
CREATE TABLE IF NOT EXISTS clients (
    id BIGSERIAL PRIMARY KEY,
    first_names VARCHAR(200) NOT NULL,
    last_names VARCHAR(200) NOT NULL,
    document_id VARCHAR(50) NOT NULL UNIQUE,
    birth_date DATE NOT NULL,
    mobile_phone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Travel Packages table
CREATE TABLE IF NOT EXISTS travel_packages (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    destination VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    additional_services TEXT,
    base_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Reservations table
CREATE TABLE IF NOT EXISTS reservations (
    id BIGSERIAL PRIMARY KEY,
    reservation_number VARCHAR(50) NOT NULL UNIQUE,
    client_id BIGINT NOT NULL REFERENCES clients(id) ON DELETE RESTRICT,
    package_id BIGINT NOT NULL REFERENCES travel_packages(id) ON DELETE RESTRICT,
    reservation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    passenger_count INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING_PAYMENT',
    total_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Passengers table
CREATE TABLE IF NOT EXISTS passengers (
    id BIGSERIAL PRIMARY KEY,
    reservation_id BIGINT NOT NULL REFERENCES reservations(id) ON DELETE CASCADE,
    first_name VARCHAR(200) NOT NULL,
    last_name VARCHAR(200) NOT NULL,
    document_id VARCHAR(50) NOT NULL,
    birth_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_clients_document_id ON clients(document_id);
CREATE INDEX IF NOT EXISTS idx_clients_email ON clients(email);
CREATE INDEX IF NOT EXISTS idx_clients_full_name ON clients(first_names, last_names);
CREATE INDEX IF NOT EXISTS idx_reservations_client_id ON reservations(client_id);
CREATE INDEX IF NOT EXISTS idx_reservations_package_id ON reservations(package_id);
CREATE INDEX IF NOT EXISTS idx_reservations_number ON reservations(reservation_number);
CREATE INDEX IF NOT EXISTS idx_passengers_reservation_id ON passengers(reservation_id);

