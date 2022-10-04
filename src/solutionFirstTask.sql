CREATE TABLE library (
    id BIGINT PRIMARY KEY,
    name varchar(255) NOT NULL,
    author varchar(255),
    isbn varchar(255) UNIQUE
);