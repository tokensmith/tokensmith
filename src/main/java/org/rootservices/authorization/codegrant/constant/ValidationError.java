package org.rootservices.authorization.codegrant.constant;

/**
 * Created by tommackenzie on 2/2/15.
 */
public enum ValidationError {
    EMPTY_VALUE (1, "The item in the list has a empty value", "invalid_request", "the value is empty"),
    MORE_THAN_ONE_ITEM (1, "The list has more than one item", "invalid_request", "duplicate values are present"),
    EMPTY_LIST (3, "The list is empty", "invalid_request", "no value is present"),
    NULL (4, "The list is null.", "invalid_request", "no value is present");

    private int code;
    private final String message;
    private final String error;
    private final String errorDescription;

    private ValidationError(int code, String message, String error, String errorDescription) {
        this.code = code;
        this.message = message;
        this.error=error;
        this.errorDescription=errorDescription;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }
}
