package net.toknsmith.login.http;

public enum ContentType {
    FORM_URL_ENCODED ("application/x-www-form-urlencoded"),
    JWT ("application/jwt"),
    JSON_UTF_8 ("application/json;charset=UTF-8");

    private String value;

    ContentType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
