CREATE DATABASE userDB;
USE userDB;

CREATE TABLE login (
    login_id INT AUTO_INCREMENT PRIMARY KEY,  
    username VARCHAR(50) NOT NULL,            
    password VARCHAR(50) NOT NULL             
);

INSERT INTO login (username, password) 
VALUES ('admin', 'password'), 
       ('user1', 'mypassword'),
       ('user2', 'anotherpassword');

CREATE TABLE books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    year INT NOT NULL
);

INSERT INTO books (title, author, year)
VALUES ("Hello world", "Me", 2025),
       ("End World", "Enemy", 1998);