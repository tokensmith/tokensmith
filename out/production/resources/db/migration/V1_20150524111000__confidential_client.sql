CREATE TABLE confidential_client (
    uuid            UUID PRIMARY KEY,
    client_uuid     UUID references client(uuid) NOT NULL,
    password        bytea NOT NULL,
    created_at      timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);
