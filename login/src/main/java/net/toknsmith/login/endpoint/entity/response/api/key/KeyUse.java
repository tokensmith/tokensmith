package net.toknsmith.login.endpoint.entity.response.api.key;

import com.fasterxml.jackson.annotation.JsonValue;

public enum KeyUse {
    SIGNATURE ("sig"),
    ENCRYPTION ("enc");

    String value;

    KeyUse(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
