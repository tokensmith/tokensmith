
create table access_request_scopes (
    uuid                       UUID PRIMARY KEY,
    access_request_uuid        UUID references access_request(uuid) NOT NULL,
    scope_uuid                 UUID references scope(uuid) NOT NULL,
    created_at                 timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);