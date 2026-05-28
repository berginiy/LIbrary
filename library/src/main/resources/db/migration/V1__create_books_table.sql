CREATE TABLE IF NOT EXISTS books (
                                     id          VARCHAR(36)  NOT NULL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    author      VARCHAR(255) NOT NULL,
    year        INTEGER      NOT NULL,
    isbn        VARCHAR(20)  NOT NULL UNIQUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
    );