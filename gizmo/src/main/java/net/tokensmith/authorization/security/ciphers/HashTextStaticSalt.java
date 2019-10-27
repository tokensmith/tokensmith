package net.tokensmith.authorization.security.ciphers;


public interface HashTextStaticSalt {
   String run(String plainText);
}
