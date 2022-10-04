create table employee
(
    id   bigint auto_increment primary key,
    firstname varchar(255),
    lastname varchar(255)
);
    CREATE TABLE chat(
        id bigint auto_increment primary key,
        name VARCHAR(64) NOT NULL UNIQUE
    );