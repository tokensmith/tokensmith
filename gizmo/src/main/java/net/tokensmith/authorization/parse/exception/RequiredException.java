package net.tokensmith.authorization.parse.exception;

/**
 * Created by tommackenzie on 3/30/17.
 */
public class RequiredException extends Exception {
    private String field;
    private String param;
    private Object target;

    public RequiredException() {
    }

    public RequiredException(String message, Throwable cause, String field, String param) {
        super(message, cause);
        this.field = field;
        this.param = param;
    }

    public RequiredException(String message, Throwable cause, String field, String param, Object target) {
        super(message, cause);
        this.field = field;
        this.param = param;
        this.target = target;
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

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }
}
