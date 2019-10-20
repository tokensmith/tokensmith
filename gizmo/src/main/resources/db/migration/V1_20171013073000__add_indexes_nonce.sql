alter table nonce add constraint nonce_nonce_unique unique (nonce);
CREATE INDEX nonce_spent on nonce (spent);
CREATE INDEX nonce_revoked on nonce (revoked);
CREATE INDEX nonce_expires_at on nonce (expires_at);
