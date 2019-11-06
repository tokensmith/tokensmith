package net.tokensmith.authorization.nonce.message;


public enum MessageType {
    FORGOT_PASSWORD, PASSWORD_WAS_RESET, WELCOME;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}