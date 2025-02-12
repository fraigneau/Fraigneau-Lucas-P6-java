DROP DATABASE IF EXISTS pay_my_buddy;
CREATE DATABASE pay_my_buddy;
USE pay_my_buddy;

DROP TABLE IF EXISTS User;
CREATE TABLE User (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    CONSTRAINT unique_email UNIQUE (email),
);

DROP TABLE IF EXISTS UserConnections;
CREATE TABLE UserConnections (
    user_id INT NOT NULL,
    connection_id INT NOT NULL,
    PRIMARY KEY (user_id, connection_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES User (id) ON DELETE CASCADE,
    CONSTRAINT fk_connection FOREIGN KEY (connection_id) REFERENCES User (id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS Transaction;
CREATE TABLE Transaction (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_sender FOREIGN KEY (sender_id) REFERENCES User (id),
    CONSTRAINT fk_receiver FOREIGN KEY (receiver_id) REFERENCES User (id)
);