CREATE TABLE auth_code_token (
    id                 UUID PRIMARY KEY,
    auth_code_id       UUID references auth_code(uuid) NOT NULL,
    token_id           UUID references token(uuid) NOT NULL,
    created_at         timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);