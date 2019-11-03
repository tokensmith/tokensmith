alter table token
add column active_token varchar(1024) NOT NULL,
add column rotate_token varchar(1024),
add constraint token_active_token_unique unique (active_token);

alter table auth_code
add column active_code varchar(1024) NOT NULL,
add column rotate_code varchar(1024),
add constraint auth_code_active_code_unique unique (active_code);

alter table confidential_client
add column active_password varchar(1024) NOT NULL,
add column rotate_password varchar(1024);

alter table resource_owner
add column active_password varchar(1024) NOT NULL,
add column rotate_password varchar(1024);

alter table nonce
add column active_nonce varchar(1024) NOT NULL,
add column rotate_nonce varchar(1024);