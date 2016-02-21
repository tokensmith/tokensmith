--// create_database
-- Migration SQL that makes the change goes here.
CREATE TABLE rsa_private_key (
    uuid                UUID PRIMARY KEY,
    use                 varchar(245) NOT NULL,
    modulus             bytea NOT NULL,
    public_exponent     bytea NOT NULL,
    private_exponent    bytea NOT NULL,
    prime_p             bytea NOT NULL,
    prime_q             bytea NOT NULL,
    prime_exponent_p    bytea NOT NULL,
    prime_exponent_q    bytea NOT NULL,
    crt_coefficient     bytea NOT NULL,
    active              boolean NOT NULL default false,
    created_at          timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--//@UNDO
-- SQL to undo the change goes here.