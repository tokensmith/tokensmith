--// create_database
-- Migration SQL that makes the change goes here.
CREATE TABLE client (
    uuid            UUID PRIMARY KEY,
    response_type   varchar(100),
    redirect_uri    varchar(254),
    created_at      date NOT NULL DEFAULT CURRENT_DATE
);

--//@UNDO
-- SQL to undo the change goes here.
