package net.tokensmith.authorization.security.ciphers;

/**
 * Created by tommackenzie on 5/4/15.
 */
public interface IsTextEqualToHash {
    boolean run(String plainText, String hashText);
}
