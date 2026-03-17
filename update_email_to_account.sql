-- SQL script to rename email column to account in existing database
-- First, backup your database before running this script!

-- Rename the column
ALTER TABLE users CHANGE COLUMN email account VARCHAR(255) UNIQUE NOT NULL;

-- Rename the index
ALTER INDEX idx_users_email ON users RENAME TO idx_users_account;

-- Verify the change
DESCRIBE users;