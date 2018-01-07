
CREATE TABLE auth_code (
    uuid                UUID PRIMARY KEY,
    code                bytea NOT NULL,
    resource_owner_uuid UUID references resource_owner(uuid),
    client_uuid         UUID references client(uuid),
    expires_at          timestamp with time zone NOT NULL,
    created_at          timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);