package org.rootservices.authorization.constant;

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
    SCOPES_NOT_SUPPORTED (17, "scope is not available for this client"),

    STATE_EMPTY_VALUE (18, "states first item is a empty value"),
    STATE_MORE_THAN_ONE_ITEM (19, "states has more than one value."),

    CLIENT_NOT_FOUND (20, "client was not found"),
    RESPONSE_TYPE_MISMATCH (21, "response type provided does not match client's response type."),
    REDIRECT_URI_MISMATCH (22, "redirect uri provided does not match client's."),
    RESPONSE_TYPE_NOT_CODE (23, "response type provided is not CODE"),

    RESOURCE_OWNER_NOT_FOUND (24, "The resource owner was not found"),
    PASSWORD_MISMATCH (25, "Password did not match"),
    UNSUPPORTED_ENCODING (26, ""),

    AUTH_CODE_NOT_FOUND(27, "authorization code was not found"),
    DUPLICATE_KEY (28, "there was a duplicate key in the payload"),
    INVALID_PAYLOAD (29, "the payload couldn't be parsed"),
    MISSING_KEY (30, "A key is missing from the payload"),
    REDIRECT_URI_INVALID (31, "redirect uri is invalid"),
    GRANT_TYPE_INVALID (32, "grant type is invalid"),
    UNKNOWN_KEY (33, "A key is unrecognized");

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
