-- Insérer des utilisateurs
INSERT INTO
    users (
        id,
        username,
        email,
        password,
        balance
    )
VALUES (
        1,
        'Alice',
        'alice@example.com',
        'password123',
        100.00
    ),
    (
        2,
        'Bob',
        'bob@example.com',
        'password123',
        200.00
    ),
    (
        3,
        'Charlie',
        'charlie@example.com',
        'password123',
        300.00
    ),
    (
        4,
        'test',
        't@t.t',
        '123123',
        100.00
    ) (
        4,
        'aaaa',
        'a@a.a,
        ' 123123 ',
        100.00
    ),
    (
        5,
        ' bbbb ',
        ' b @b.b,
        '123123',
        100.00
    ),
    (
        6,
        'cccc',
        'c@c.c,
        ' 123123 ',
        100.00
    ),
    (
        7,
        ' Lika ',
        ' abel @g.com,
        '123123',
        100.00
    ),

-- Insérer des relations d'amitié entre les utilisateurs
INSERT INTO
    user_connections (user_id, connection_id)
VALUES (1, 2),
    (2, 1),

-- Insérer des transactions entre utilisateurs
INSERT INTO
    user_transactions (
        id,
        sender_id,
        receiver_id,
        description,
        amount,
        created_at
    )
VALUES (
        1,
        1,
        2,
        'Remboursement',
        15.00,
        NOW()
    ),
    (2, 2, 1, 'Cdx', 30.00, NOW());