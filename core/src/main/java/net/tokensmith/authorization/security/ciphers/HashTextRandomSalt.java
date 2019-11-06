package net.tokensmith.authorization.security.ciphers;


public interface HashTextRandomSalt {
    String run(String plainText);
}
