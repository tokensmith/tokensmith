package org.rootservices.authorization.security.ciphers;


public interface HashTextRandomSalt {
    String run(String plainText);
}
