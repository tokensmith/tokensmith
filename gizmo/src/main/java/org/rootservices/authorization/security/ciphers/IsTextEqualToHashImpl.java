package org.rootservices.authorization.security.ciphers;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;


@Component
public class IsTextEqualToHashImpl implements  IsTextEqualToHash {

    @Override
    public boolean run(String plainText, String hashText) {
        return BCrypt.checkpw(plainText, hashText);
    }
}
