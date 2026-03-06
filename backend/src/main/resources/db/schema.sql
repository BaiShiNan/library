-- Create Database
CREATE DATABASE IF NOT EXISTS digital_library
    DEFAULT CHARACTER SET = 'utf8mb4';
USE digital_library;

-- Create Users Table
CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  name VARCHAR(100) NOT NULL,
  role VARCHAR(20) DEFAULT 'USER',
  avatar_url VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_users_email (email)
);

-- Create Categories Table
CREATE TABLE IF NOT EXISTS categories (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  sort_order INT DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Insert Initial Categories
INSERT IGNORE INTO categories (name, description, sort_order) VALUES
('文学', '小说、诗歌、散文等文学作品', 1),
('科技', '计算机、工程、科学等科技类图书', 2),
('历史', '历史、传记、考古等历史相关图书', 3),
('经济', '经济、金融、管理等商业类图书', 4),
('艺术', '绘画、音乐、设计等艺术类图书', 5);

-- Create Books Table
CREATE TABLE IF NOT EXISTS books (
  id INT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  author VARCHAR(255) NOT NULL,
  isbn VARCHAR(20) UNIQUE,
  category_id INT,
  description TEXT,
  cover_url VARCHAR(500),
  file_url VARCHAR(500) NOT NULL,
  rating DECIMAL(3,2) DEFAULT 0.00,
  page_count INT DEFAULT 0,
  publish_date DATE,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_category (category_id),
  INDEX idx_author (author),
  INDEX idx_created_at (created_at),
  FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Create Reading History Table
CREATE TABLE IF NOT EXISTS reading_history (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  book_id INT NOT NULL,
  progress DECIMAL(5,2) DEFAULT 0.00,
  last_read_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY unique_user_book (user_id, book_id),
  INDEX idx_user_id (user_id),
  INDEX idx_book_id (book_id),
  INDEX idx_last_read (last_read_at),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

-- Create Favorites Table
CREATE TABLE IF NOT EXISTS favorites (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  book_id INT NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY unique_user_book (user_id, book_id),
  INDEX idx_user_id (user_id),
  INDEX idx_book_id (book_id),
  INDEX idx_created (created_at),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);
