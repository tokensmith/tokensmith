package org.rootservices.authorization.codegrant.factory.constants;

/**
 * Created by tommackenzie on 2/1/15.
 */
public enum ValidationMessage {
    EMPTY_VALUE ("Empty Value"),
    MORE_THAN_ONE_ITEM ("More than One item"),
    EMPTY_LIST ("List is empty"),
    NULL ("Paramter is null");

    private final String message;

    private ValidationMessage(String message) {
        this.message = message;
    }

}
