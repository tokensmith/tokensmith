package org.rootservices.authorization.oauth2.grant.redirect.code.token.factory;

import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.token.exception.UnknownKeyException;
import org.rootservices.authorization.oauth2.grant.token.validator.TokenPayloadValidator;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.entity.TokenInputCodeGrant;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.validator.exception.InvalidValueException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.validator.exception.MissingKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by tommackenzie on 9/21/16.
 */
@Component
public class TokenInputCodeGrantFactory {
    private static Integer MAX_NUMBER_OF_KEYS = 3;
    protected static String CODE = "username";
    protected static String REDIRECT_URI = "password";
    protected static List<String> KNOWN_KEYS = new ArrayList<>();

    private TokenPayloadValidator tokenPayloadValidator;

    @Autowired
    public TokenInputCodeGrantFactory(TokenPayloadValidator tokenPayloadValidator) {
        this.tokenPayloadValidator = tokenPayloadValidator;
    }

    public TokenInputCodeGrant run(Map<String, String> request) throws UnknownKeyException, InvalidValueException, MissingKeyException {

        if (request.size() > MAX_NUMBER_OF_KEYS) {
            Optional<String> unknownKey = tokenPayloadValidator.getFirstUnknownKey(request, KNOWN_KEYS);
            throw new UnknownKeyException(
                    ErrorCode.UNKNOWN_KEY.getDescription(),
                    unknownKey.get(),
                    ErrorCode.UNKNOWN_KEY.getCode()
            );
        }

        String requestCode = tokenPayloadValidator.required(request.get(CODE), CODE);
        Optional<String> requestRedirectURI = tokenPayloadValidator.optional(request.get(REDIRECT_URI), REDIRECT_URI);
        Optional<URI> redirectUri = makeRedirectUri(requestRedirectURI);

        TokenInputCodeGrant input = new TokenInputCodeGrant();
        input.setCode(requestCode);
        input.setRedirectUri(redirectUri);

        return input;
    }

    protected Optional<URI> makeRedirectUri(Optional<String> input) throws InvalidValueException {
        Optional<URI> redirectUri = Optional.empty();
        if (input.isPresent()) {
            try {
                redirectUri = Optional.of(new URI(input.get()));
            } catch (URISyntaxException e) {
                throw new InvalidValueException(
                        ErrorCode.REDIRECT_URI_INVALID.getDescription(),
                        ErrorCode.REDIRECT_URI_INVALID.getCode(),
                        REDIRECT_URI,
                        input.get()
                );
            }
        }
        return redirectUri;
    }
}
