CREATE TABLE IF NOT EXISTS category (
    id SERIAL primary key,
    name varchar(50) not null,
    type varchar(50) not null,
    author varchar(50) default null,
    photo_url varchar(250) default null
    );

CREATE TABLE IF NOT EXISTS option(
     id SERIAL primary key,
     cat_id bigint,
     name varchar(255) not null,
    photo_url varchar(255) default null,

    constraint fk_cat
    FOREIGN KEY (cat_id)
    REFERENCES category(id)
    )