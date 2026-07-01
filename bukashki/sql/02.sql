
create table if not exists insect_photos (
    id bigint primary key,
    insect_id bigint not null references insects(id),
    object_key text not null
)