package net.toknsmith.login.endpoint.entity.request;

public enum GrantType {
    AUTHORIZATION_CODE, PASSWORD, REFRESH_TOKEN, CLIENT_CREDENTIALS;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
