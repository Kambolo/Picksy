CREATE TABLE IF NOT EXISTS user (
    id SERIAL primary key,
    name varchar(50) not null,
    type varchar(50) not null,
    author_id bigint default null,
    photo_url varchar(250) default null
    );