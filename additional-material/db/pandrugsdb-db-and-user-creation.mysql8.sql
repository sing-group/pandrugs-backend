DROP DATABASE IF EXISTS `pandrugsdb`;
CREATE DATABASE `pandrugsdb` CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE USER IF NOT EXISTS 'pandrugsdb'@'localhost' IDENTIFIED BY 'pandrugsdb';
GRANT ALL ON `pandrugsdb`.* to 'pandrugsdb'@'localhost';
FLUSH PRIVILEGES;
