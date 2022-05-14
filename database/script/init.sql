CREATE DATABASE machine_learning_database;
CREATE USER 'springuser'@'%' IDENTIFIED BY 'ThePassword';
GRANT ALL ON machine_learning_database.* TO 'springuser'@'%';
USE machine_learning_database