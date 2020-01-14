package net.toknsmith.login.endpoint.entity.request;

public enum FormFields {
    GRANT_TYPE, USERNAME, PASSWORD, SCOPE, REFRESH_TOKEN, CODE, REDIRECT_URI;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
