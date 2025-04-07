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

-- Create login table with enhanced security features
CREATE TABLE IF NOT EXISTS login (
    formno VARCHAR(10),
    cardno VARCHAR(16) PRIMARY KEY,
    pin VARCHAR(100) NOT NULL, -- Stores SHA-256 hashed PIN with salt
    failed_attempts INT DEFAULT 0, -- Tracks failed login attempts
    is_locked INT DEFAULT 0, -- Indicates if account is locked (1=locked, 0=active)
    security_question VARCHAR(255), -- Stores security question for PIN reset
    security_answer VARCHAR(255), -- Stores answer to security question
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

-- Add indexes for better performance and security
CREATE INDEX idx_cardno_pin ON login(cardno, pin);
CREATE INDEX idx_signup_name ON signup(name);
CREATE INDEX idx_signup_email ON signup(email);
CREATE INDEX idx_login_attempts ON login(failed_attempts); -- Index for quicker lockout checks
CREATE INDEX idx_login_security ON login(cardno, security_question); -- Index for security verification

-- Create trigger to enforce PIN format and card number validation
DELIMITER //
CREATE TRIGGER before_login_insert 
BEFORE INSERT ON login
FOR EACH ROW
BEGIN
    -- Verify card number format
    IF LENGTH(NEW.cardno) != 16 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Card number must be exactly 16 digits';
    END IF;
    
    -- Verify required security fields
    IF NEW.security_question IS NULL OR LENGTH(TRIM(NEW.security_question)) = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Security question is required';
    END IF;
    
    IF NEW.security_answer IS NULL OR LENGTH(TRIM(NEW.security_answer)) = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Security answer is required';
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
    l.is_locked,
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

-- Create view for security monitoring
CREATE OR REPLACE VIEW security_status AS
SELECT
    l.cardno,
    s.name,
    s.email,
    l.failed_attempts,
    l.is_locked,
    CASE 
        WHEN l.is_locked = 1 THEN 'Locked'
        WHEN l.failed_attempts >= 2 THEN 'At Risk'
        ELSE 'Normal'
    END as security_status
FROM login l
JOIN signup s ON l.formno = s.formno
ORDER BY l.failed_attempts DESC;

-- Add sample data (optional, commented out for safety)
/*
INSERT INTO signup (formno, name, fname, dob, gender, email, marital, address, city, state, pin, religion, category, income, education, occupation, pan, aadhar, senior, existing, account_type, facilities)
VALUES ('1234', 'John Doe', 'Richard Doe', '1990-01-01', 'Male', 'john@example.com', 'Single', '123 Main St', 'Mumbai', 'Maharashtra', '400001', 'Hindu', 'General', '<2.5L', 'Graduate', 'Salaried', 'ABCDE1234F', '123456789012', 'No', 'No', 'Savings', 'ATM Card,Internet Banking');
*/

-- Procedure to unlock all accounts (for administrative use only)
DELIMITER //
CREATE PROCEDURE unlock_all_accounts()
BEGIN
    UPDATE login SET is_locked = 0, failed_attempts = 0;
    SELECT 'All accounts have been unlocked.' AS result;
END //
DELIMITER ;

-- Procedure to unlock a specific account by card number
DELIMITER //
CREATE PROCEDURE unlock_account(IN card_number VARCHAR(16))
BEGIN
    DECLARE affected_rows INT;
    
    UPDATE login SET is_locked = 0, failed_attempts = 0
    WHERE cardno = card_number;
    
    SET affected_rows = ROW_COUNT();
    
    IF affected_rows > 0 THEN
        SELECT CONCAT('Account ', card_number, ' has been unlocked.') AS result;
    ELSE
        SELECT CONCAT('Account ', card_number, ' not found or already unlocked.') AS result;
    END IF;
END //
DELIMITER ;