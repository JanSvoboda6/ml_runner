INSERT INTO role(name) VALUES('ROLE_USER');
INSERT INTO role(name) VALUES('ROLE_ADMIN');
INSERT INTO user(username, password, is_verified) VALUES ('jan@jan.com', '$2a$10$8nHq5vTlNUiAtsMkoo1TTuJPb6Q2lvrO35tgVVbO9ALkFq42gNp52', true);
INSERT INTO user_role(user_id, role_id) VALUES (1, 1);