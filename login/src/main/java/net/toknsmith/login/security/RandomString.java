package net.toknsmith.login.security;

import java.math.BigInteger;
import java.security.SecureRandom;


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
