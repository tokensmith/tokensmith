package org.rootservices.authorization.security;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 6/6/15.
 */
@Component
public class HashTextStaticSaltImpl implements HashTextStaticSalt {

    private String salt;

    @Override
    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public String run(String plainText) {
        return BCrypt.hashpw(plainText, "$2a$10$oBKpYtNOYLWIlZHBXU/Vhe");
    }
}
