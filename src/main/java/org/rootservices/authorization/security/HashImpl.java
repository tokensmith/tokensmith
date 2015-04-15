package org.rootservices.authorization.security;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 4/13/15.
 */
@Component
public class HashImpl implements Hash {

    public HashImpl() {}

    @Override
    public String run(String plainText) {
        return BCrypt.hashpw(plainText, BCrypt.gensalt());
    }
}
