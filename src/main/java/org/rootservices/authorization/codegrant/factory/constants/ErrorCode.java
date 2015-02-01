package org.rootservices.authorization.codegrant.factory.constants;

/**
 * Created by tommackenzie on 2/1/15.
 */
public enum ErrorCode {
    EMPTY_VALUE (1),
    MORE_THAN_ONE_ITEM (2),
    EMPTY_LIST (3),
    NULL (4),
    DATA_TYPE (5);

    private final int code;

    private ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
