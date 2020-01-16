alter table token
add column lead_auth_time timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP;