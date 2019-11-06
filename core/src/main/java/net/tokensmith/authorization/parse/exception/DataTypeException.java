package net.tokensmith.authorization.parse.exception;


public class DataTypeException extends Exception {
    private String field;
    private String param;
    private String value;

    public DataTypeException(String message, Throwable cause, String value) {
        super(message, cause);
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
