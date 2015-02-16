package org.rootservices.authorization.codegrant.constant;

/**
 * Created by tommackenzie on 2/4/15.
 */
public enum ErrorCode {

    // resource owner
    CLIENT_ID_EMPTY_VALUE (1, "client ids first item is a empty value."),
    CLIENT_ID_MORE_THAN_ONE_ITEM (2, "client ids has more than one value."),
    CLIENT_ID_EMPTY_LIST (3, "client ids is a empty list"),
    CLIENT_ID_NULL (4, "client ids is null"),
    CLIENT_ID_DATA_TYPE (5, "client ids first item is not a UUID"),

    // client or resource owner -- needs feedback from db.
    RESPONSE_TYPE_EMPTY_VALUE (6, "response types first item is a empty value."),
    RESPONSE_TYPE_MORE_THAN_ONE_ITEM (7, "response types has more than one value."),
    RESPONSE_TYPE_EMPTY_LIST (8, "response types is a empty list"),
    RESPONSE_TYPE_NULL (9, "response types ids is null"),
    RESPONSE_TYPE_DATA_TYPE (10, "response types first item is not valid"),

    // resource owner
    REDIRECT_URI_EMPTY_VALUE (11, "redirect uris first item is a empty value."),
    REDIRECT_URI_MORE_THAN_ONE_ITEM (12, "redirect uris has more than one value."),
    REDIRECT_URI_DATA_TYPE (13, "redirect uris first item is not a URI"),

    // client or resource owner -- needs feedback from db.
    SCOPES_EMPTY_VALUE (14, "scopes first item is a empty value."),
    SCOPES_MORE_THAN_ONE_ITEM (15, "scopes has more than one value."),
    SCOPES_DATA_TYPE (16, "scopes first item is not a supported scope"),

    STATE_EMPTY_VALUE (17, "states first item is a empty value"),
    STATE_MORE_THAN_ONE_ITEM (18, "states has more than one value."),

    CLIENT_NOT_FOUND (19, "client was not found"),
    RESPONSE_TYPE_MISMATCH (20, "response type provided does not match client's response type."),
    REDIRECT_URI_MISMATCH (21, "redirect uri provided does not match client's response type."),
    RESPONSE_TYPE_NOT_CODE (22, "response type provided is not CODE");

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
