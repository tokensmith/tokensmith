package net.tokensmith.authorization.persistence.entity;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by tommackenzie on 2/12/16.
 */
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
}
