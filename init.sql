CREATE DATABASE IF NOT EXISTS complaint_db;
USE complaint_db;

DROP TABLE IF EXISTS complaints;

CREATE TABLE complaints (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255),
    department VARCHAR(255),
    room_number VARCHAR(100),
    subject VARCHAR(255),
    complaint_text TEXT,
    file_name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);