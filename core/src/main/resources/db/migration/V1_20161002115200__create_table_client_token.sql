CREATE TABLE client_token (
    id                 UUID PRIMARY KEY,
    client_id          UUID references client(id) NOT NULL,
    token_id           UUID references token(id) NOT NULL,
    created_at         timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);