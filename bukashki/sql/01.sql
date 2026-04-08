create table if not exists users (
    id bigserial primary key,
    username varchar(100) not null,
    email varchar(255) unique not null,
    password_hash text not null
)