package net.toknsmith.login.exception;


/**
 * Used to indicate when a an issued occurred validating the id token's signature.
 *
 */
public class JwtException extends Exception {
    public JwtException(String message) {
        super(message);
    }

    public JwtException(String message, Throwable cause) {
        super(message, cause);
    }
}
