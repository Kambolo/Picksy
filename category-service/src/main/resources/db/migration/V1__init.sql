CREATE TABLE IF NOT EXISTS category (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    author_id BIGINT DEFAULT NULL,
    photo_url VARCHAR(250) DEFAULT NULL,
    description VARCHAR(500) DEFAULT NULL,
    views BIGINT DEFAULT 0,
    created TIMESTAMPTZ DEFAULT NOW(),
    is_public BOOLEAN DEFAULT True
    );


CREATE TABLE IF NOT EXISTS option(
     id SERIAL primary key,
     cat_id bigint not null,
     name varchar(255) not null,
    photo_url varchar(255) default null,

    constraint fk_cat
    FOREIGN KEY (cat_id)
    REFERENCES category(id)
    )