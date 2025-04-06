-- Create database if not exists
CREATE DATABASE IF NOT EXISTS atm;
USE atm;

-- Create signup table
CREATE TABLE IF NOT EXISTS signup (
    formno VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    fname VARCHAR(100) NOT NULL,
    dob DATE NOT NULL,
    gender VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    marital VARCHAR(20) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    pin VARCHAR(10) NOT NULL,
    religion VARCHAR(50) NOT NULL,
    category VARCHAR(50) NOT NULL,
    income VARCHAR(50) NOT NULL,
    education VARCHAR(50) NOT NULL,
    occupation VARCHAR(50) NOT NULL,
    pan VARCHAR(20) NOT NULL,
    aadhar VARCHAR(20) NOT NULL,
    senior VARCHAR(5) NOT NULL,
    existing VARCHAR(5) NOT NULL,
    account_type VARCHAR(50) NOT NULL,
    facilities VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_pan (pan),
    UNIQUE KEY unique_aadhar (aadhar)
);

-- Create login table
CREATE TABLE IF NOT EXISTS login (
    formno VARCHAR(10),
    cardno VARCHAR(16) PRIMARY KEY,
    pin VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (formno) REFERENCES signup(formno) ON DELETE CASCADE
);

-- Create bank table for transactions
CREATE TABLE IF NOT EXISTS bank (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pin VARCHAR(100) NOT NULL,
    date TIMESTAMP NOT NULL,
    type VARCHAR(20) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_pin (pin),
    INDEX idx_date (date)
);

-- Add indexes for better performance
CREATE INDEX idx_cardno_pin ON login(cardno, pin);
CREATE INDEX idx_signup_name ON signup(name);
CREATE INDEX idx_signup_email ON signup(email);

-- Create trigger to enforce PIN format
DELIMITER //
CREATE TRIGGER before_login_insert 
BEFORE INSERT ON login
FOR EACH ROW
BEGIN
    IF LENGTH(NEW.cardno) != 16 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Card number must be exactly 16 digits';
    END IF;
END;//
DELIMITER ;

-- Create views for common queries
CREATE OR REPLACE VIEW account_summary AS
SELECT 
    s.formno,
    s.name,
    s.email,
    l.cardno,
    s.account_type,
    s.created_at as account_created
FROM signup s
JOIN login l ON s.formno = l.formno;

CREATE OR REPLACE VIEW transaction_summary AS
SELECT 
    b.date,
    b.type,
    b.amount,
    l.cardno,
    s.name
FROM bank b
JOIN login l ON b.pin = l.pin
JOIN signup s ON l.formno = s.formno;

-- Add sample data (optional, commented out for safety)
/*
INSERT INTO signup (formno, name, fname, dob, gender, email, marital, address, city, state, pin, religion, category, income, education, occupation, pan, aadhar, senior, existing, account_type, facilities)
VALUES ('1234', 'John Doe', 'Richard Doe', '1990-01-01', 'Male', 'john@example.com', 'Single', '123 Main St', 'Mumbai', 'Maharashtra', '400001', 'Hindu', 'General', '<2.5L', 'Graduate', 'Salaried', 'ABCDE1234F', '123456789012', 'No', 'No', 'Savings', 'ATM Card,Internet Banking');
*/