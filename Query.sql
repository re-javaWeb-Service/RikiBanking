use rikkei_banking;

SELECT id, username, password, is_active, role_id
FROM users
WHERE username = 'admin';

SHOW TABLES;

SELECT * from rikkei_banking.token_black_list;