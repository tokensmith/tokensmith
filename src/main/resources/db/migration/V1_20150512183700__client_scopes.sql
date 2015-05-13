
create table client_scopes (
    uuid                UUID PRIMARY KEY,
    client_uuid         UUID references client(uuid) NOT NULL,
    scope_uuid          UUID references scope(uuid) NOT NULL,
    created_at          timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);