package org.rootservices.authorization.security;

import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 6/6/15.
 */
public interface HashTextStaticSalt {
   String run(String plainText);
}
