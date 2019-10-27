package net.tokensmith.authorization.persistence.exceptions;

import java.util.Optional;

/**
 * Created by tommackenzie on 7/15/15.
 */
public class DuplicateRecordException extends Exception {
    private Optional<String> key;

    // empty constructor, used by tests
    public DuplicateRecordException() {}

    public DuplicateRecordException(String message, Throwable cause){
        super(message, cause);
        this.key = Optional.empty();
    }

    public DuplicateRecordException(String message, Throwable cause, Optional<String> key) {
        super(message, cause);
        this.key = key;
    }

    public Optional<String> getKey() {
        return key;
    }
}
