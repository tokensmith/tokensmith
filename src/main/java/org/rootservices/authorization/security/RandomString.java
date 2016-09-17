package org.rootservices.authorization.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by tommackenzie on 4/16/15.
 * http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
 *
 */
@Component
public class RandomString {

    private SecureRandom secureRandom = new SecureRandom();

    public RandomString() {}

    public RandomString(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }

    public String run() {
        return new BigInteger(130, secureRandom).toString(32);
    }
}
