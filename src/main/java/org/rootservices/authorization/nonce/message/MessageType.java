package org.rootservices.authorization.nonce.message;


public enum MessageType {
    RESET_PASSWORD, WELCOME;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}