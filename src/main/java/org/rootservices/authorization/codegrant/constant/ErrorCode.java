package org.rootservices.authorization.codegrant.constant;

/**
 * Created by tommackenzie on 2/4/15.
 */
public enum ErrorCode {
    EMPTY_VALUE (1, "The item in the list has a empty value"),
    MORE_THAN_ONE_ITEM (1, "The list has more than one item"),
    EMPTY_LIST (3, "The list is empty"),
    NULL (4, "The list is null."),
    DATA_TYPE (5, "Could not coerce input to desired data type"),
    CLIENT_ID_INVALID(6, "Client id did not pass validation"),
    REDIRECT_INVALID (7, "Redirect uri did not pass validation"),
    RESPONSE_TYPE_INVALID (8, "Response Type did not pass validation"),
    SCOPES_INVALID (9, "Scopes did not pass validation"),
    STATE_INVALID (10, "State did not pass validation");

    private int code;
    private final String message;

    private ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }
}
