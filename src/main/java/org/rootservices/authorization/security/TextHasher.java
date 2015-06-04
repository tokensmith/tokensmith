package org.rootservices.authorization.security;

/**
 * Created by tommackenzie on 4/13/15.
 */
public interface TextHasher {
    String run(String plainText);
}
