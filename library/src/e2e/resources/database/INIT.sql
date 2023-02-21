CREATE TABLE user
(
   id INT NOT NULL PRIMARY KEY,
   name VARCHAR (255)
);
CREATE TABLE book
(
   id INT NOT NULL PRIMARY KEY,
   title VARCHAR (255),
   author VARCHAR (255),
   available BIT,
   userId INT,
   FOREIGN KEY (userId) REFERENCES user (id)
);

INSERT INTO user (id, name) VALUES(-1, "default-user");