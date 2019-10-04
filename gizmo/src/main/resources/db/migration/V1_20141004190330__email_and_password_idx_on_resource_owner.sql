-- // email_and_password_idx_on_auth_user
-- Migration SQL that makes the change goes here.
CREATE INDEX email_and_password_idx on resource_owner (email, password);


-- //@UNDO
-- SQL to undo the change goes here.


