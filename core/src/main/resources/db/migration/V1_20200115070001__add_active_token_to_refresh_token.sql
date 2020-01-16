alter table refresh_token
add column active_token varchar(1024) NOT NULL,
add column rotate_token varchar(1024),
add constraint refresh_token_active_token_unique unique (active_token);

alter table refresh_token
drop column access_token;