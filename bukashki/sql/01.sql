create table if not exists users (
    id bigserial primary key,
    first_name text not null,
    last_name text not null,
    email varchar(255) not null unique,
    avatar_url text
)

create table if not exists user_credentials (
    id bigserial primary key,
    user_id bigint not null unique references users(id) on delete cascade,
    login varchar(50) not null unique,
    password_hash text not null,
    enabled boolean not null default true
)