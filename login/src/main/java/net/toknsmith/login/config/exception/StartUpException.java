package net.toknsmith.login.config.exception;


/**
 * Generic exception that is used when something is not configured correctly
 * which will result in the sdk to not work correctly.
 */
public class StartUpException extends RuntimeException {

    public StartUpException(String message) {
        super(message);
    }

    public StartUpException(String message, Throwable cause) {
        super(message, cause);
    }
}
