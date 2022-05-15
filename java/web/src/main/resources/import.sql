INSERT INTO role(name) VALUES('ROLE_USER');
INSERT INTO role(name) VALUES('ROLE_ADMIN');
INSERT INTO user(username, password) VALUES ('jan@jan.com', '$2a$10$1SIUaUNKlkqshtozaZWbaezYNB.2jQlbFZNfTlBXqnVvzBqaAE1xC');
INSERT INTO user_role(user_id, role_id) VALUES (1, 1);