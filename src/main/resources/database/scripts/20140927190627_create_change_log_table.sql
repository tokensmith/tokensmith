-- // create_change_log_table
-- Migration SQL that makes the change goes here.
CREATE TABLE CHANGELOG (
ID NUMERIC(20,0) NOT NULL,
APPLIED_AT VARCHAR(25) NOT NULL,
DESCRIPTION VARCHAR(255) NOT NULL
);


-- //@UNDO
-- SQL to undo the change goes here.


