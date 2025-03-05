-- Insert test users
INSERT INTO 
    users (id, username, email, password, balance)
VALUES
    (1, 'testuser', 'test@example.com', '$2a$10$rB1d.Kpje/Oj6Bn/ZBUDfO7xux6hN5g.eXMWoc37s2isvg6Z1Bnq2', 1000.00), -- password: 123123
    (2, 'janesmith', 'jane@example.com', '$2a$10$rB1d.Kpje/Oj6Bn/ZBUDfO7xux6hN5g.eXMWoc37s2isvg6Z1Bnq2', 500.00),
    (3, 'bobthomas', 'bob@example.com', '$2a$10$rB1d.Kpje/Oj6Bn/ZBUDfO7xux6hN5g.eXMWoc37s2isvg6Z1Bnq2', 750.00);

-- Create connections between users
INSERT INTO
    user_connections (user_id, connection_id)
VALUES
    (1, 2), -- testuser is connected with janesmith
    (2, 1), -- janesmith is connected with testuser (bidirectional)
    (2, 3), -- janesmith is connected with bobthomas
    (3, 2); -- bobthomas is connected with janesmith (bidirectional)

-- Add some transactions
INSERT INTO
    user_transactions (id, sender_id, receiver_id, description, amount, created_at)
VALUES
    (1, 1, 2, 'Lunch payment', 25.50, '2023-10-15 12:30:00'),
    (2, 2, 1, 'Movie tickets', 30.00, '2023-10-20 18:45:00'),
    (3, 2, 3, 'Shared taxi', 15.75, '2023-10-25 22:15:00');