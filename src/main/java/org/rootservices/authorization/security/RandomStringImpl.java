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
public class RandomStringImpl implements RandomString {

    private SecureRandom secureRandom = new SecureRandom();

    public RandomStringImpl() {}

    public RandomStringImpl(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }

    @Override
    public String run() {
        return new BigInteger(130, secureRandom).toString(32);
    }
}
