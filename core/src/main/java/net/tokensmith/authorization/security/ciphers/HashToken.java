package net.tokensmith.authorization.security.ciphers;


public interface HashToken {
   String run(String plainText);
}
