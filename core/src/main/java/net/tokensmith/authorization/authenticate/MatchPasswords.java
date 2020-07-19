package net.tokensmith.authorization.authenticate;

import net.tokensmith.authorization.authenticate.exception.UnauthorizedException;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.security.ciphers.IsTextEqualToHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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


    public boolean run(String plainTextPassword, String hashedPassword) throws UnauthorizedException {

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
