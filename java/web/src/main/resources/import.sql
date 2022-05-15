INSERT INTO role(name) VALUES('ROLE_USER');
INSERT INTO role(name) VALUES('ROLE_ADMIN');
INSERT INTO user(username, password, is_verified) VALUES ('jan@jan.com', '$2a$10$s9/DTr1gVXyYUJnMaCxzCujByJNTotvwEhsj0Ho9Y2mJF9qYykzsG', true);
INSERT INTO user_role(user_id, role_id) VALUES (1, 1);