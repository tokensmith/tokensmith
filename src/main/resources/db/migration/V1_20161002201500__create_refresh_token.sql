CREATE TABLE refresh_token (
    id                 UUID PRIMARY KEY,
    token_id           UUID references token(id) NOT NULL,
    token              bytea NOT NULL,
    revoked            boolean default false,
    expires_at         timestamp with time zone NOT NULL,
    created_at         timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);