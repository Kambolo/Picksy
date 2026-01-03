CREATE TABLE profile (
    user_id bigint not null,
    avatar_url varchar(255) default null,
    bio text default null
)