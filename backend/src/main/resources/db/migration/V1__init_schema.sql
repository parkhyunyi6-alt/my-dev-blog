CREATE TABLE users (
    id              BIGSERIAL       PRIMARY KEY,
    google_id       VARCHAR(255)    NOT NULL UNIQUE,
    email           VARCHAR(255)    NOT NULL UNIQUE,
    name            VARCHAR(100)    NOT NULL,
    profile_image_url VARCHAR(500),
    role            VARCHAR(20)     NOT NULL DEFAULT 'GUEST',
    created_at      TIMESTAMP       NOT NULL
);

CREATE TABLE category_groups (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(100)    NOT NULL,
    display_order   INT             NOT NULL DEFAULT 0,
    created_at      TIMESTAMP       NOT NULL
);

CREATE TABLE categories (
    id                  BIGSERIAL   PRIMARY KEY,
    category_group_id   BIGINT      NOT NULL REFERENCES category_groups(id),
    name                VARCHAR(100) NOT NULL,
    display_order       INT         NOT NULL DEFAULT 0,
    created_at          TIMESTAMP   NOT NULL
);

CREATE TABLE posts (
    id          BIGSERIAL       PRIMARY KEY,
    user_id     BIGINT          NOT NULL REFERENCES users(id),
    category_id BIGINT          NOT NULL REFERENCES categories(id),
    title       VARCHAR(255)    NOT NULL,
    content     TEXT            NOT NULL,
    view_count  INT             NOT NULL DEFAULT 0,
    heart_count INT             NOT NULL DEFAULT 0,
    created_at  TIMESTAMP       NOT NULL,
    updated_at  TIMESTAMP       NOT NULL
);

CREATE TABLE tags (
    id          BIGSERIAL       PRIMARY KEY,
    name        VARCHAR(50)     NOT NULL UNIQUE,
    created_at  TIMESTAMP       NOT NULL
);

CREATE TABLE post_tags (
    id      BIGSERIAL   PRIMARY KEY,
    post_id BIGINT      NOT NULL REFERENCES posts(id),
    tag_id  BIGINT      NOT NULL REFERENCES tags(id),
    UNIQUE (post_id, tag_id)
);

CREATE TABLE post_likes (
    id          BIGSERIAL       PRIMARY KEY,
    post_id     BIGINT          NOT NULL REFERENCES posts(id),
    user_id     BIGINT          REFERENCES users(id),
    device_id   VARCHAR(255),
    created_at  TIMESTAMP       NOT NULL,
    UNIQUE (post_id, user_id),
    UNIQUE (post_id, device_id)
);
