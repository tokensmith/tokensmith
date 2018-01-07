alter table access_request alter column auth_code_uuid drop not null;
alter table access_request drop constraint access_request_auth_code_uuid_fkey;