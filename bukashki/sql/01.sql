create table if not exists users (
    id bigserial primary key,
    email varchar(255) unique not null,
    password_hash text not null
)