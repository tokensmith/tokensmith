
create table access_request_scopes (
    uuid                UUID PRIMARY KEY,
    auth_code_uuid      UUID references auth_code(uuid) NOT NULL,
    scope_uuid          UUID references scope(uuid) NOT NULL,
    created_at          timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);