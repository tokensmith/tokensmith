package net.tokensmith.authorization.security;

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
    private static Integer BASE_32 = 32;
    private static Integer MAGIC_FIVE = 5;

    public RandomString() {}

    public RandomString(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }

    public String run() {
        return new BigInteger(130, secureRandom).toString(BASE_32);
    }

    public String run(Integer numChars) {
        return new BigInteger(numChars * MAGIC_FIVE, secureRandom).toString(BASE_32);
    }
}
