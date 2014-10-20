--// create_database
-- Migration SQL that makes the change goes here.
CREATE TABLE auth_user (
    uuid        UUID PRIMARY KEY,
    email       varchar(245) UNIQUE NOT NULL,
    password    bytea NOT NULL,
    created_at  date NOT NULL DEFAULT CURRENT_DATE
);

--//@UNDO
-- SQL to undo the change goes here.


