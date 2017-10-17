package org.rootservices.authorization.security.ciphers;


public interface HashTextStaticSalt {
   String run(String plainText);
}
