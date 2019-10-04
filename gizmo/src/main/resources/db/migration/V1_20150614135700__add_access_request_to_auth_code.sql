alter table auth_code add column access_request_uuid UUID references access_request(uuid);

update auth_code
set access_request_uuid = ar.uuid
from access_request ar
where auth_code.uuid = ar.auth_code_uuid;