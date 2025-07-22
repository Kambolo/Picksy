CREATE TABLE profile (
    id serial primary key,
    user_id bigint not null,
    avatar_url varchar(255) default null,
    bio text default null
)