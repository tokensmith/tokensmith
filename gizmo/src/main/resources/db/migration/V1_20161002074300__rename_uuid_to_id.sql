alter table client rename uuid to id;

alter table scope rename uuid to id;

alter table client_scopes rename uuid to id;
alter table client_scopes rename client_uuid to client_id;
alter table client_scopes rename scope_uuid to scope_id;

alter table confidential_client rename uuid to id;
alter table confidential_client rename client_uuid to client_id;

alter table resource_owner rename uuid to id;

alter table access_request rename uuid to id;
alter table access_request rename client_uuid to client_id;
alter table access_request rename resource_owner_uuid to resource_owner_id;

alter table access_request_scopes rename uuid to id;
alter table access_request_scopes rename access_request_uuid to access_request_id;
alter table access_request_scopes rename scope_uuid to scope_id;

alter table auth_code rename uuid to id;
alter table auth_code rename access_request_uuid to access_request_id;

alter table rsa_private_key rename uuid to id;

alter table token rename uuid to id;