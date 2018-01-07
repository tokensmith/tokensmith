CREATE TABLE token_lead_token (
    id                 UUID PRIMARY KEY,
    token_id           UUID references token(id) NOT NULL,
    lead_token_id      UUID references token(id) NOT NULL,
    created_at         timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);