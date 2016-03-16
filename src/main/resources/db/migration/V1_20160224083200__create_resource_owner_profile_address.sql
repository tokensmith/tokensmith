CREATE TABLE resource_owner_profile_address (
    id                              UUID PRIMARY KEY,
    resource_owner_profile_id       UUID references resource_owner_profile(id) NOT NULL,
    street_address                  varchar(245) NOT NULL,
    street_address2                 varchar(245),
    locality                        varchar(100) NOT NULL,
    region                          varchar(100) NOT NULL,
    postal_code                     varchar(50) NOT NULL,
    country                         varchar(50) NOT NULL,
    created_at                      timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                      timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);