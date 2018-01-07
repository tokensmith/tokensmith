--// create_database
-- Migration SQL that makes the change goes here.
CREATE TABLE resource_owner (
    uuid        UUID PRIMARY KEY,
    email       varchar(245) UNIQUE NOT NULL,
    password    bytea NOT NULL,
    created_at  timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--//@UNDO
-- SQL to undo the change goes here.


