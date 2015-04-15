
create table auth_request (
    uuid                UUID PRIMARY KEY,
    response_type       varchar(100) NOT NULL,
    redirect_uri        varchar(254),
    auth_code_uuid      UUID references auth_code(uuid),
    created_at          timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);