package net.toknsmith.login.http;

public enum HeaderValue {
    LOGIN_SDK ("login-sdk-java"),
    // TODO: make version dynamic with properties file.
    LOGIN_SDK_VERSION("0.0.1-SNAPSHOT");

    private String value;

    HeaderValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
