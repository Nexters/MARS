CREATE TABLE IF NOT EXISTS "cookies"
(
    "cookie_id"       bigserial,
    "title"           varchar(255),
    "price"           bigint,
    "content"         text,
    "image_url"       varchar(255),
    "owned_user_id"   bigint,
    "author_user_id"  bigint,
    "created_at"      timestamp,
    "cookie_tag_id"   bigint,
    "contractAddress" varchar(255),
    "tokenAddress"    varchar(255),
    "status"          varchar(10),
    PRIMARY KEY ("cookie_id")
);

CREATE TABLE IF NOT EXISTS "cookie_tags"
(
    "cookie_tag_id" bigserial,
    "cookie_id"     bigint,
    "tag_id"        bigint,
    PRIMARY KEY ("cookie_tag_id")
);

CREATE TABLE IF NOT EXISTS "tags"
(
    "tag_id"        bigserial,
    "name"          varchar(20),
    "cookie_tag_id" bigint,
    PRIMARY KEY ("tag_id")
);

CREATE TABLE IF NOT EXISTS "users"
(
    "user_id"        bigserial,
    "nickname"       varchar(100),
    "introduction"   varchar(255),
    "profile_url"    varchar(255),
    "wallet_address" varchar(255),
    "status"         varchar(10),
    PRIMARY KEY ("user_id")
);

CREATE TABLE IF NOT EXISTS "inquiries"
(
    "inquiry_id"       bigserial,
    "title"            varchar(255),
    "sender_user_id"   bigint,
    "receiver_user_id" bigint,
    PRIMARY KEY ("inquiry_id")
);

CREATE TABLE IF NOT EXISTS "view_counts"
(
    "view_count_id" bigserial,
    "user_id"       bigint,
    "cookie_id"     bigint,
    "count"         bigint,
    "created_at"    timestamp,
    PRIMARY KEY ("view_count_id")
);

CREATE TABLE IF NOT EXISTS "user_tags"
(
    "user_tag_id" bigserial,
    "user_id"     bigint,
    "tag_id"      bigint,
    PRIMARY KEY ("user_tag_id")
);
