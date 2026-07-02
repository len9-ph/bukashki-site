create sequence if not exists insect_photos_seq start with 1 increment by 50;

create table if not exists insect_photos (
    id bigint primary key,
    insect_id bigint not null references insects(id),
    object_key text not null unique
)