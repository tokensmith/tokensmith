
create table access_request (
    uuid                UUID PRIMARY KEY,
    redirect_uri        varchar(254),
    auth_code_uuid      UUID references auth_code(uuid),
    created_at          timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);