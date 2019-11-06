package net.tokensmith.authorization.nonce.message;

public enum Topic {
    MAILER;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
