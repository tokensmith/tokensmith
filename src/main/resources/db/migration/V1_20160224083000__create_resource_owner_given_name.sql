CREATE TABLE resource_owner_profile_given_name (
    id                              UUID PRIMARY KEY,
    resource_owner_profile_id       UUID references resource_owner_profile(id) NOT NULL,
    given_name                      varchar(50) NOT NULL,
    created_at                      timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                      timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);