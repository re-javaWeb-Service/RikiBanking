use rikkei_banking;

SELECT id, username, password, is_active, role_id
FROM users
WHERE username = 'admin';

SHOW TABLES;