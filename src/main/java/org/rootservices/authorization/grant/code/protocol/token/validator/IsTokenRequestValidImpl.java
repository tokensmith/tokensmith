package org.rootservices.authorization.grant.code.protocol.token.validator;

import org.apache.commons.validator.routines.UrlValidator;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.protocol.token.TokenRequest;
import org.rootservices.authorization.grant.code.protocol.token.validator.exception.GrantTypeInvalidException;
import org.rootservices.authorization.grant.code.protocol.token.validator.exception.InvalidValueException;
import org.rootservices.authorization.grant.code.protocol.token.validator.exception.MissingKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Optional;

/**
 * Created by tommackenzie on 7/4/15.
 */
@Component
public class IsTokenRequestValidImpl implements IsTokenRequestValid {

    private UrlValidator urlValidator;

    @Autowired
    public IsTokenRequestValidImpl(UrlValidator urlValidator) {
        this.urlValidator = urlValidator;
    }

    public boolean run(TokenRequest tokenRequest) throws InvalidValueException, MissingKeyException {
        isGrantTypeValid(tokenRequest.getGrantType());
        isCodeValid(tokenRequest.getCode());
        isRedirectURIValid(tokenRequest.getRedirectUri());

        return true;
    }

    private boolean isGrantTypeValid(String grantType) throws MissingKeyException, InvalidValueException {
        if (grantType != null && grantType.equals("authorization_code")) {
            return true;
        } else if ( grantType != null && !grantType.equals("authorization_code")) {
            throw new GrantTypeInvalidException(ErrorCode.GRANT_TYPE_INVALID.getMessage(), ErrorCode.GRANT_TYPE_INVALID.getCode(), "grant_type", grantType);
        }
        throw new MissingKeyException("missing grant_type", "grant_type");
    }

    private boolean isCodeValid(String code) throws MissingKeyException {
        if (code != null) {
            return true;
        }
        throw new MissingKeyException("missing code", "code");
    }

    private boolean isRedirectURIValid(Optional<URI> redirectUri) throws InvalidValueException, MissingKeyException {
        if (redirectUri.isPresent() && urlValidator.isValid(redirectUri.get().toString())) {
            return true;
        } else if (!redirectUri.isPresent()) {
            return true;
        }
        throw new InvalidValueException(ErrorCode.REDIRECT_URI_INVALID.getMessage(), ErrorCode.REDIRECT_URI_INVALID.getCode(), "redirect_uri", redirectUri.get().toString());
    }
}
