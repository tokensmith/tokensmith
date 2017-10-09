CREATE TABLE nonce (
    id                 UUID PRIMARY KEY,
    nonce_type_id      UUID references nonce_type(id) NOT NULL,
    resource_owner_id  UUID references resource_owner(id) NOT NULL,
    nonce              bytea NOT NULL,
    revoked            boolean NOT NULL DEFAULT False,
    spent              boolean NOT NULL DEFAULT False,
    expires_at         timestamp with time zone NOT NULL,
    created_at         timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);