package org.rootservices.authorization.authenticate;

import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.security.ciphers.IsTextEqualToHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * Created by tommackenzie on 5/25/15.
 */
@Component
public class MatchPasswords {
    private IsTextEqualToHash isTextEqualToHash;

    @Autowired
    public MatchPasswords(IsTextEqualToHash isTextEqualToHash) {
        this.isTextEqualToHash = isTextEqualToHash;
    }


    public boolean run(String plainTextPassword, byte[] bytesHashedPassword) throws UnauthorizedException {

        String hashedPassword = "";
        try {
            hashedPassword = new String(bytesHashedPassword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnauthorizedException(
                    ErrorCode.UNSUPPORTED_ENCODING.getDescription(),
                    e, ErrorCode.UNSUPPORTED_ENCODING.getCode());
        }

        boolean passwordsMatch = isTextEqualToHash.run(
                plainTextPassword, hashedPassword
        );

        if ( !passwordsMatch ) {
            throw new UnauthorizedException(
                    ErrorCode.PASSWORD_MISMATCH.getDescription(),
                    ErrorCode.PASSWORD_MISMATCH.getCode());
        }
        return true;
    }
}
