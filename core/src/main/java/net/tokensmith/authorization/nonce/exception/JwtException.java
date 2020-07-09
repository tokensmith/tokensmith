package net.tokensmith.authorization.nonce.exception;

public class JwtException extends Exception {
    public JwtException(String message, Throwable cause) {
        super(message, cause);
    }
}
