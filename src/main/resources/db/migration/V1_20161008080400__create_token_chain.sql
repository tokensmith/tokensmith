CREATE TABLE token_chain (
    id                 UUID PRIMARY KEY,
    token_id           UUID references token(id) NOT NULL,
    prev_token_id      UUID references token(id) NOT NULL,
    refresh_token_id   UUID references refresh_token(id) NOT NULL,
    created_at         timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);