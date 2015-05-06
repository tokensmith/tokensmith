package org.rootservices.authorization.security;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 5/4/15.
 */
@Component
public class IsTextEqualToHashImpl implements  IsTextEqualToHash {

    @Override
    public boolean run(String plainText, String hashText) {
        return BCrypt.checkpw(plainText, hashText);
    }
}
