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

-- Insert Initial Books (Sample Data)
INSERT IGNORE INTO books (title, author, isbn, category_id, description, cover_url, file_url, rating, page_count, publish_date) VALUES
('百年孤独', '加西亚·马尔克斯', '9787544253994', 1, '魔幻现实主义文学代表作，描写了布恩迪亚家族七代人的传奇故事。', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=800', 'sample.pdf', 4.8, 360, '2011-06-01'),
('三体', '刘慈欣', '9787536692930', 2, '中国科幻文学里程碑之作，讲述了人类文明与三体文明的信息交流、生死搏杀及两个文明在宇宙中的兴衰历程。', 'https://images.unsplash.com/photo-1614726365723-49cfae9e096d?auto=format&fit=crop&q=80&w=800', 'sample.pdf', 4.9, 302, '2008-01-01'),
('人类简史', '尤瓦尔·赫拉利', '9787508647357', 3, '从认知革命、农业革命到科学革命，理清了影响人类发展的重大历史节点。', 'https://images.unsplash.com/photo-1457369804613-52c61a468e7d?auto=format&fit=crop&q=80&w=800', 'sample.pdf', 4.7, 440, '2014-11-01'),
('设计心理学', '唐纳德·诺曼', '9787508653631', 5, '强调以用户为中心的设计哲学，是设计领域的经典之作。', 'https://images.unsplash.com/photo-1586075010923-2dd4570fb338?auto=format&fit=crop&q=80&w=800', 'sample.pdf', 4.6, 288, '2015-05-01'),
('富爸爸穷爸爸', '罗伯特·清崎', '9787229048077', 4, '通过两个爸爸的理财观念对比，揭示了富人为什么越来越富。', 'https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?auto=format&fit=crop&q=80&w=800', 'sample.pdf', 4.5, 248, '2011-09-01'),
('活着', '余华', '9787506365437', 1, '讲述了福贵一生的跌宕起伏，展示了生命的韧性。', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&q=80&w=800', 'sample.pdf', 4.9, 195, '2012-08-01'),
('深入理解计算机系统', 'Randal E. Bryant', '9787111544937', 2, '程序员必读经典，深入剖析计算机系统的运行原理。', 'https://images.unsplash.com/photo-1550751827-4bd374c3f58b?auto=format&fit=crop&q=80&w=800', 'sample.pdf', 4.9, 800, '2016-11-01'),
('明朝那些事儿', '当年明月', '9787505722460', 3, '以幽默诙谐的语言讲述了明朝三百年的历史。', 'https://images.unsplash.com/photo-1461360370896-922624d12aa1?auto=format&fit=crop&q=80&w=800', 'sample.pdf', 4.8, 300, '2006-09-01');

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
