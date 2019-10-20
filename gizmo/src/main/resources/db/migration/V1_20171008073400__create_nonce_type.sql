CREATE TABLE nonce_type (
    id                 UUID PRIMARY KEY,
    name               varchar(254),
    seconds_to_expiry  integer NOT NULL DEFAULT 86400,
    created_at         timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);