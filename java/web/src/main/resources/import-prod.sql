INSERT IGNORE INTO role(id, name) VALUES(1, 'ROLE_USER');
INSERT IGNORE INTO role(id, name) VALUES(2, 'ROLE_ADMIN');
INSERT IGNORE INTO user(username, password, is_verified) VALUES ('jan@jan.com', '$2a$10$s9/DTr1gVXyYUJnMaCxzCujByJNTotvwEhsj0Ho9Y2mJF9qYykzsG', true);
INSERT IGNORE INTO user_role(user_id, role_id) VALUES (1, 1);