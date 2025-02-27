DROP DATABASE IF EXISTS pay_my_buddy;

CREATE DATABASE pay_my_buddy;

USE pay_my_buddy;

DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    balance DECIMAL(10, 2) DEFAULT 0.00,
    CONSTRAINT unique_email UNIQUE (email)
);

DROP TABLE IF EXISTS user_connections;

CREATE TABLE user_connections (
    user_id BIGINT NOT NULL,
    connection_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, connection_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_connection FOREIGN KEY (connection_id) REFERENCES users (id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS transactions;

CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sender FOREIGN KEY (sender_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_receiver FOREIGN KEY (receiver_id) REFERENCES users (id) ON DELETE CASCADE
);