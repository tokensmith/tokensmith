
create table scope (
    uuid                UUID PRIMARY KEY,
    name                varchar(254),
    created_at          timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);