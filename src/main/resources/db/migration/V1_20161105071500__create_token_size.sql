CREATE TABLE token_size (
    id                          UUID PRIMARY KEY,
    access_token_size           Integer NOT NULL,
    authorization_code_size     Integer NOT NULL,
    refresh_token_size          Integer NOT NULL,
    created_at                  timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);