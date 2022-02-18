CREATE TABLE IF NOT EXISTS "cookies"
(
    "cookie_id"          bigserial,
    "title"              varchar(255),
    "price"              bigint,
    "content"            text,
    "image_url"          varchar(255),
    "owned_user_id"      bigint,
    "author_user_id"     bigint,
    "created_at"         timestamp,
    "status"             varchar(10),
    "tx_hash"            varchar(255) unique,
    "nft_token_id"       bigint unique,
    "from_block_address" bigint unique,
    "categoryId"         bigint,
    PRIMARY KEY ("cookie_id")
);

CREATE TABLE IF NOT EXISTS "categories"
(
    "category_id" bigserial,
    "name"        varchar(20),
    "color"       varchar(15),
    PRIMARY KEY ("category_id")
);

CREATE TABLE IF NOT EXISTS "users"
(
    "user_id"        bigserial,
    "nickname"       varchar(100),
    "introduction"   varchar(255),
    "profile_url"    varchar(255),
    "background_url" varchar(255),
    "wallet_address" varchar(255),
    "status"         varchar(10),
    PRIMARY KEY ("user_id")
);

CREATE TABLE IF NOT EXISTS "asks"
(
    "ask_id"           bigserial,
    "title"            varchar(255),
    "status"           varchar(20),
    "sender_user_id"   bigint,
    "receiver_user_id" bigint,
    PRIMARY KEY ("ask_id")
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

CREATE TABLE IF NOT EXISTS "user_categories"
(
    "user_category_id" bigserial,
    "user_id"          bigint,
    "category_id"      bigint,
    PRIMARY KEY ("user_category_id")
);
