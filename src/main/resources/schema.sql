create table cookies
(
    id             bigserial,
    title          varchar(255),
    price          bigint,
    content        text,
    image_url      varchar(255),
    author_user_id bigint,
    created_at     timestamp,
    cookie_tag_id  bigint
);

create table tags
(
    id            bigserial,
    name          varchar(20),
    cookie_tag_id bigint
);

create table users
(
    id             bigserial,
    nickname       varchar(100),
    introduction   varchar(255),
    profile_url    varchar(255),
    wallet_address varchar(255),
    status         varchar(10)
);

create table inqueries
(
    id      bigserial,
    title   varchar(255),
    user_id bigint
);

create table user_tags
(
    user_id bigint
    tag_id bigint
);
