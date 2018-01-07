CREATE TABLE client_response_type (
    id                 UUID PRIMARY KEY,
    client_id          UUID references client(uuid) NOT NULL,
    response_type_id   UUID references response_type(id) NOT NULL,
    created_at         timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);