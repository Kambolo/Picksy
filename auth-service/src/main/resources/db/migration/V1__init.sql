CREATE TABLE IF NOT EXISTS users (
    id SERIAL primary key,
    username varchar(100) not null,
    password varchar(100) not null,
    email varchar(100) not null,
    Role varchar(25) not null
    );