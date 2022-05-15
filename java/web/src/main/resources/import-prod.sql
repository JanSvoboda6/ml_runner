INSERT IGNORE INTO role(id, name) VALUES(1, 'ROLE_USER');
INSERT IGNORE INTO role(id, name) VALUES(2, 'ROLE_ADMIN');
INSERT INTO user(username, password, isVerified) VALUES ('jan@jan.com', '$2a$10$1SIUaUNKlkqshtozaZWbaezYNB.2jQlbFZNfTlBXqnVvzBqaAE1xC', true);
INSERT INTO user_role(user_id, role_id) VALUES (1, 1);