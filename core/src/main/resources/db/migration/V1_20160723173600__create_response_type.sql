CREATE TABLE response_type (
    id                 UUID PRIMARY KEY,
    name               varchar(100) NOT NULL,
    created_at         timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);