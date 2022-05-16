INSERT IGNORE INTO role(id, name) VALUES(1, 'ROLE_USER');
INSERT IGNORE INTO role(id, name) VALUES(2, 'ROLE_ADMIN');
INSERT IGNORE INTO user(username, password, is_verified) VALUES ('jan@jan.com', '$2a$10$8nHq5vTlNUiAtsMkoo1TTuJPb6Q2lvrO35tgVVbO9ALkFq42gNp52', true);
INSERT IGNORE INTO user_role(user_id, role_id) VALUES (1, 1);