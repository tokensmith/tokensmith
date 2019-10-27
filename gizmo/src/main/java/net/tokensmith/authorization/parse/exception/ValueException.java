package net.tokensmith.authorization.parse.exception;

/**
 * Created by tommackenzie on 4/4/17.
 */
public class ValueException extends Exception {
    private String field;
    private String param;
    private String value;

    public ValueException(String message, String field, String param, String value) {
        super(message);
        this.field = field;
        this.param = param;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
