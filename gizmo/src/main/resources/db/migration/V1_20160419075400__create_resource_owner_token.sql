CREATE TABLE resource_owner_token (
    id                 UUID PRIMARY KEY,
    resource_owner_id  UUID references resource_owner(uuid) NOT NULL,
    token_id           UUID references token(uuid) NOT NULL,
    created_at         timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);