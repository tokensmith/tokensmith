alter table access_request add column resource_owner_uuid UUID references resource_owner(uuid);

update access_request
set resource_owner_uuid = ac.resource_owner_uuid
from auth_code ac
where access_request.auth_code_uuid = ac.uuid;

alter table access_request alter column resource_owner_uuid set not null;