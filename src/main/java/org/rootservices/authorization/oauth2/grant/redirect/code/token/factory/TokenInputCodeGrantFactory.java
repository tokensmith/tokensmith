package org.rootservices.authorization.oauth2.grant.redirect.code.token.factory;

import org.apache.commons.validator.routines.UrlValidator;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.token.exception.UnknownKeyException;
import org.rootservices.authorization.oauth2.grant.token.validator.TokenPayloadValidator;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.entity.TokenInputCodeGrant;
import org.rootservices.authorization.oauth2.grant.token.exception.InvalidValueException;
import org.rootservices.authorization.oauth2.grant.token.exception.MissingKeyException;
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
    protected static String CODE = "code";
    protected static String REDIRECT_URI = "redirect_uri";
    protected static List<String> KNOWN_KEYS = Arrays.asList("grant_type", "code", "redirect_uri");

    private TokenPayloadValidator tokenPayloadValidator;
    private UrlValidator urlValidator;

    @Autowired
    public TokenInputCodeGrantFactory(TokenPayloadValidator tokenPayloadValidator, UrlValidator urlValidator) {
        this.tokenPayloadValidator = tokenPayloadValidator;
        this.urlValidator = urlValidator;
    }

    public TokenInputCodeGrant run(Map<String, String> request) throws UnknownKeyException, InvalidValueException, MissingKeyException {

        Optional<String> unknownKey = tokenPayloadValidator.getFirstUnknownKey(request, KNOWN_KEYS);
        if (unknownKey.isPresent()) {
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
        if (input.isPresent() && urlValidator.isValid(input.get())) {
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
        } else if (input.isPresent()) { // not a valid url
            throw new InvalidValueException(
                    ErrorCode.REDIRECT_URI_INVALID.getDescription(),
                    ErrorCode.REDIRECT_URI_INVALID.getCode(),
                    REDIRECT_URI,
                    input.get()
            );
        }
        return redirectUri;
    }
}
