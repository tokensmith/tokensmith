create table token (
    uuid               UUID PRIMARY KEY,
    auth_code_uuid     UUID references auth_code(uuid) NOT NULL,
    expires_at         timestamp with time zone NOT NULL,
    created_at         timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);