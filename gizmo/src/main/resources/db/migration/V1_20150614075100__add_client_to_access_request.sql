alter table access_request add column client_uuid UUID references client(uuid);

update access_request
set client_uuid = ac.client_uuid
from auth_code ac
where access_request.auth_code_uuid = ac.uuid;

alter table access_request alter column client_uuid set not null;