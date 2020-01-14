package net.toknsmith.login.http;

public enum  Header {
    CONTENT_TYPE ("Content-Type"),
    AUTHORIZATION ("Authorization"),
    ACCEPT ("Accept"),
    ACCEPT_ENCODING ("Accept-Encoding"),
    CONTENT_ENCODING ("Content-Encoding"),
    CORRELATION_ID ("X-Correlation-ID"),
    LOGIN_SDK ("X-Login-SDK"),
    LOGIN_SDK_VERSION ("X-Login-SDK-Version");

    private String value;

    Header(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
