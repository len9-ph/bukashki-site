create sequence if not exists users_seq start with 1 increment by 50;
create sequence if not exists user_credentials_seq start with 1 increment by 50;
create sequence if not exists insects_seq start with 1 increment by 50;

create table if not exists users (
    user_id bigint primary key,
    first_name text not null,
    last_name text not null,
    email varchar(255) not null unique,
    avatar_url text
);

create table if not exists user_credentials (
    id bigint primary key,
    user_id bigint not null unique references users(user_id) on delete cascade,
    login varchar(50) not null unique,
    password_hash text not null,
    enabled boolean not null default true
);

create table if not exists insects (
    id bigint primary key,
    user_id bigint not null references users(user_id) on delete cascade,
    created_at timestamptz not null default now(),
    insect_name text not null,
    insect_description text
);
