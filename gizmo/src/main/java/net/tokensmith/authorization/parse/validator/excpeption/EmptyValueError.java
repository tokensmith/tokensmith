package net.tokensmith.authorization.parse.validator.excpeption;


public class EmptyValueError extends Exception {
    public EmptyValueError(String message) {
        super(message);
    }
}