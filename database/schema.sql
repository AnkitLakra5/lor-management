-- LOR Management System Database Schema
-- MySQL 8.0+

CREATE DATABASE IF NOT EXISTS lor_management;
USE lor_management;

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS lor_requests;
DROP TABLE IF EXISTS pdf_documents;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS admin_students;
DROP TABLE IF EXISTS admin_professors;

-- Admin pre-uploaded student data
CREATE TABLE admin_students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    registration_number VARCHAR(50) NOT NULL UNIQUE,
    examination_number VARCHAR(50) NOT NULL UNIQUE,
    course VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Admin pre-uploaded professor data
CREATE TABLE admin_professors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    user_id VARCHAR(50) NOT NULL UNIQUE,
    department VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Users table for all registered users
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- BCrypt hashed
    role ENUM('ADMIN', 'STUDENT', 'PROFESSOR') NOT NULL,
    
    -- Student specific fields
    registration_number VARCHAR(50) NULL,
    examination_number VARCHAR(50) NULL UNIQUE,
    course VARCHAR(255) NULL,
    
    -- Professor specific fields
    user_id VARCHAR(50) NULL UNIQUE,
    department VARCHAR(255) NULL,
    
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_student_fields CHECK (
        (role = 'STUDENT' AND registration_number IS NOT NULL AND examination_number IS NOT NULL AND course IS NOT NULL)
        OR (role != 'STUDENT')
    ),
    CONSTRAINT chk_professor_fields CHECK (
        (role = 'PROFESSOR' AND user_id IS NOT NULL AND department IS NOT NULL)
        OR (role != 'PROFESSOR')
    ),
    CONSTRAINT chk_admin_fields CHECK (
        (role = 'ADMIN' AND registration_number IS NULL AND examination_number IS NULL AND course IS NULL AND user_id IS NULL AND department IS NULL)
        OR (role != 'ADMIN')
    )
);

-- LOR requests table
CREATE TABLE lor_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    professor_id BIGINT NOT NULL,
    
    -- Auto-filled student data
    student_name VARCHAR(255) NOT NULL,
    registration_number VARCHAR(50) NOT NULL,
    examination_number VARCHAR(50) NOT NULL,
    course VARCHAR(255) NOT NULL,
    
    -- Form fields
    semester VARCHAR(50) NOT NULL,
    session VARCHAR(50) NOT NULL,
    class_roll_number VARCHAR(50) NOT NULL,
    institute_company VARCHAR(255) NOT NULL,
    
    -- Request status
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    professor_comments TEXT NULL,
    
    -- Timestamps
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign keys
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (professor_id) REFERENCES users(id) ON DELETE CASCADE
);

-- PDF documents table for generated LORs
CREATE TABLE pdf_documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lor_request_id BIGINT NOT NULL UNIQUE,
    reference_number VARCHAR(100) NOT NULL UNIQUE,
    file_path VARCHAR(500) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    generated_by BIGINT NOT NULL, -- Professor who generated
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign keys
    FOREIGN KEY (lor_request_id) REFERENCES lor_requests(id) ON DELETE CASCADE,
    FOREIGN KEY (generated_by) REFERENCES users(id) ON DELETE CASCADE
);

-- Indexes for better performance
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_examination_number ON users(examination_number);
CREATE INDEX idx_users_user_id ON users(user_id);
CREATE INDEX idx_lor_requests_student ON lor_requests(student_id);
CREATE INDEX idx_lor_requests_professor ON lor_requests(professor_id);
CREATE INDEX idx_lor_requests_status ON lor_requests(status);
CREATE INDEX idx_pdf_documents_reference ON pdf_documents(reference_number);

-- Create admin user
INSERT INTO users (name, email, password, role) VALUES 
('System Admin', 'admin@lor.system', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN');
-- Default password is 'password' (BCrypt hashed)
