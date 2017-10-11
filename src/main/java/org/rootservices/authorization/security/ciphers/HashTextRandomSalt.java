package org.rootservices.authorization.security.ciphers;

/**
 * Created by tommackenzie on 4/13/15.
 */
public interface HashTextRandomSalt {
    String run(String plainText);
}
