package net.tokensmith.authorization.nonce.message;

public enum MessageKey {
    TYPE, RECIPIENT, BASE_LINK, NONCE;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
