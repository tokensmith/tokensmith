package org.rootservices.authorization.codegrant.constant;

/**
 * Created by tommackenzie on 2/2/15.
 */
public enum ValidationError {
    EMPTY_VALUE (1, "The item in the list has a empty value"),
    MORE_THAN_ONE_ITEM (1, "The list has more than one item"),
    EMPTY_LIST (3, "The list is empty"),
    NULL (4, "The list is null.");

    private int code;
    private final String message;

    private ValidationError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

}
