package org.rootservices.authorization.openId.identity;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

/**
 * Created by tommackenzie on 9/4/16.
 * http://openid.net/specs/openid-connect-core-1_0.html#CodeIDToken
 */
@Component
public class MakeAccessTokenHash {
    private MessageDigest digestSha256;
    private Base64.Encoder urlEncoder;

    @Autowired
    public MakeAccessTokenHash(MessageDigest digestSha256, Base64.Encoder urlEncoder) {
        this.digestSha256 = digestSha256;
        this.urlEncoder = urlEncoder;
    }

    public String makeEncodedHash(String input) {
        byte[] hash;
        synchronized (this) {
            hash = digestSha256.digest(input.getBytes());
        };
        byte[] leftMostHalf = Arrays.copyOf(hash, hash.length/2);
        return urlEncoder.encodeToString(leftMostHalf);
    }
}
