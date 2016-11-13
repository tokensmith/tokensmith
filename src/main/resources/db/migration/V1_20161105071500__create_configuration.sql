CREATE TABLE configuration (
    id                                          UUID PRIMARY KEY,
    access_token_size                           Integer NOT NULL,
    authorization_code_size                     Integer NOT NULL,
    refresh_token_size                          Integer NOT NULL,
    access_token_code_seconds_to_expiry         Integer NOT NULL,
    access_token_token_seconds_to_expiry        Integer NOT NULL,
    access_token_password_seconds_to_expiry     Integer NOT NULL,
    access_token_refresh_seconds_to_expiry      Integer NOT NULL,
    access_token_client_seconds_to_expiry      Integer NOT NULL,
    authorization_code_seconds_to_expiry        Integer NOT NULL,
    refresh_token_seconds_to_expiry             Integer NOT NULL,
    created_at                                  timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                                  timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);