CREATE TABLE token_scope (
    id                 UUID PRIMARY KEY,
    token_id           UUID references token(uuid) NOT NULL,
    scope_id           UUID references scope(uuid) NOT NULL,
    created_at         timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);