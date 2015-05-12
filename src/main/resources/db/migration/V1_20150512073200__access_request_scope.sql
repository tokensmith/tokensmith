
create table access_request_to_scope (
    uuid                UUID PRIMARY KEY,
    auth_code_uuid      UUID references auth_code(uuid),
    scope_uuid          UUID references scope(uuid),
    created_at          timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);