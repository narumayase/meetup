create database meeting;
use meeting;

CREATE TABLE IF NOT EXISTS user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL
);

insert into user values (1, "root", "ff9830c42660c1dd1942844f8069b74a", "root"); #root123
insert into user values (2, "juan.padilla", "433484b5317340f5c28e085bfffc78be", "admin"); #myPass123
insert into user values (3, "rodrigo.muñoz", "482c811da5d5b4bc6d497ffa98491e38", "user"); #password123
insert into user values (4, "santiago.sata", "482c811da5d5b4bc6d497ffa98491e38", "user"); #password123
insert into user values (5, "romina.matias", "482c811da5d5b4bc6d497ffa98491e38", "user"); #password123
insert into user values (6, "maria.perez", "482c811da5d5b4bc6d497ffa98491e38", "user"); #password123
insert into user values (7, "lucia.vazquez", "482c811da5d5b4bc6d497ffa98491e38", "user"); #password123
insert into user values (8, "celeste.cid", "482c811da5d5b4bc6d497ffa98491e38", "user"); #password123
insert into user values (9, "lion.li", "482c811da5d5b4bc6d497ffa98491e38", "user"); #password123
insert into user values (10, "griselda.morina", "482c811da5d5b4bc6d497ffa98491e38", "user"); #password123

CREATE TABLE IF NOT EXISTS token (
    id INT AUTO_INCREMENT PRIMARY KEY,
    uid VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id int,
	FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS meetup (
    id INT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    beer_boxes int,
    temperature DECIMAL(19, 4),
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_meetup (
	id INT AUTO_INCREMENT PRIMARY KEY,
	user_id int,
    meetup_id int,
    checkin BOOLEAN NOT NULL DEFAULT FALSE,
	FOREIGN KEY (user_id) REFERENCES user (id),
	FOREIGN KEY (meetup_id) REFERENCES meetup (id)
);


