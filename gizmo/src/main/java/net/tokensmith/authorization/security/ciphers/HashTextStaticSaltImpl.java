package net.tokensmith.authorization.security.ciphers;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HashTextStaticSaltImpl implements HashTextStaticSalt {

    private String salt;

    @Autowired
    public HashTextStaticSaltImpl(String salt) {
        this.salt = salt;
    }

    @Override
    public String run(String plainText) {
        return BCrypt.hashpw(plainText, salt);
    }
}
