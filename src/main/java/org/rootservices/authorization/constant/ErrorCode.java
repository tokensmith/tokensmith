package org.rootservices.authorization.constant;

/**
 * Created by tommackenzie on 2/4/15.
 */
public enum ErrorCode {

    // resource owner
    CLIENT_ID_EMPTY_VALUE (1, "client_id is blank or missing"),
    CLIENT_ID_MORE_THAN_ONE_ITEM (2, "client_id has more than one value."),
    CLIENT_ID_EMPTY_LIST (3, "client_id is blank or missing"),
    CLIENT_ID_NULL (4, "client_id is null"),
    CLIENT_ID_DATA_TYPE (5, "client_id is not a UUID"),

    // client or resource owner -- needs feedback from db.
    RESPONSE_TYPE_EMPTY_VALUE (6, "response_type is blank or missing"),
    RESPONSE_TYPE_MORE_THAN_ONE_ITEM (7, "response_type has more than one value."),
    RESPONSE_TYPE_EMPTY_LIST (8, "response_type is blank or missing"),
    RESPONSE_TYPE_NULL (9, "response_type is null"),
    RESPONSE_TYPE_DATA_TYPE (10, "response_type is invalid"),

    // resource owner
    REDIRECT_URI_EMPTY_VALUE (11, "redirect_uri is blank"),
    REDIRECT_URI_MORE_THAN_ONE_ITEM (12, "redirect_uri has more than one value."),
    REDIRECT_URI_DATA_TYPE (13, "redirect_uri is not a URI"),
    REDIRECT_URI_EMPTY_LIST (14, "redirect_uri is blank"),
    REDIRECT_URI_NULL (15, "redirect_uri is null"),

    // client or resource owner -- needs feedback from db.
    SCOPES_EMPTY_VALUE (16, "scope is blank"),
    SCOPES_MORE_THAN_ONE_ITEM (17, "scope has more than one value."),
    SCOPES_DATA_TYPE (18, "scope is not valid"),
    SCOPES_NOT_SUPPORTED (19, "scope is not available for this client"),

    STATE_EMPTY_VALUE (20, "state is blank"),
    STATE_MORE_THAN_ONE_ITEM (21, "state has more than one value"),

    CLIENT_NOT_FOUND (22, "client was not found"),
    RESPONSE_TYPE_MISMATCH (23, "response_type provided does not match client's response type."),
    REDIRECT_URI_MISMATCH (24, "redirect_uri provided does not match client's."),
    RESPONSE_TYPE_NOT_CODE (25, "response_type provided is not CODE"),

    RESOURCE_OWNER_NOT_FOUND (26, "The resource owner was not found"),
    PASSWORD_MISMATCH (27, "Password did not match"),
    UNSUPPORTED_ENCODING (28, ""),

    AUTH_CODE_NOT_FOUND(29, "authorization code was not found"),
    DUPLICATE_KEY (30, "there was a duplicate key in the payload"),
    INVALID_PAYLOAD (31, "the payload couldn't be parsed"),
    EMPTY_VALUE(32, "empty value was found, expecting it to be non empty."),
    MISSING_KEY (33, "A key is missing from the payload"),
    REDIRECT_URI_INVALID (34, "redirect_uri is invalid"),
    GRANT_TYPE_INVALID (35, "grant type is invalid"),
    UNKNOWN_KEY (36, "a key is unrecognized"),
    COMPROMISED_AUTH_CODE (37, "authorization code has already been used"),

    // open id token/implicit
    NONCE_EMPTY_VALUE (38, "nonce is blank or missing"),
    NONCE_MORE_THAN_ONE_ITEM (39, "nonce has more than one value."),
    NONCE_EMPTY_LIST (40, "nonce is blank or missing"),
    NONCE_NULL (41, "nonce is null"),

    // open id - issues creating identity.
    PROFILE_NOT_FOUND (42, "resource owner doesn't have a profile"),
    SIGN_KEY_NOT_FOUND (43, "no signing key was found"),
    JWT_ENCODING_ERROR (44, "there was a problem encoding the jwt"),

    CLIENT_USERNAME_DATA_TYPE(45, "user name is not a uuid"),

    REFRESH_TOKEN_NOT_FOUND (46, "refresh token was not found"),
    COMPROMISED_REFRESH_TOKEN(47, "refresh token was already used");

    private int code;
    private final String description;

    ErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }
}
