alter table token_chain add constraint token_chain_refresh_token_id_unique unique (refresh_token_id);