package net.tokensmith.authorization.oauth2.grant.token.exception;

/**
 * Created by tommackenzie on 7/4/15.
 */
public class MissingKeyException extends Exception {
    private String key;

    public MissingKeyException(String message, String key) {
        super(message);
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
