package org.rootservices.authorization.nonce.message;


public enum MessageType {
    RESET_PASSWORD, PASSWORD_WAS_RESET, WELCOME;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}