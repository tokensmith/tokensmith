package net.toknsmith.login.http;

public enum Param {
    CLIENT_ID, RESPONSE_TYPE, REDIRECT_URI, SCOPE, STATE, NONCE;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
