package org.rootservices.authorization.oauth2.grant.password.factory;

import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.token.validator.TokenPayloadValidator;
import org.rootservices.authorization.oauth2.grant.password.entity.TokenInputPasswordGrant;
import org.rootservices.authorization.oauth2.grant.token.exception.UnknownKeyException;
import org.rootservices.authorization.oauth2.grant.token.exception.InvalidValueException;
import org.rootservices.authorization.oauth2.grant.token.exception.MissingKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by tommackenzie on 9/18/16.
 */
@Component
public class TokenInputPasswordGrantFactory {
    private static Integer MAX_NUMBER_OF_KEYS = 4;
    protected static String USER_NAME = "username";
    protected static String PASSWORD = "password";
    protected static String SCOPE = "scope";
    private static List<String> KNOWN_KEYS = Arrays.asList("grant_type", "username", "password", "scope");
    private TokenPayloadValidator tokenPayloadValidator;

    @Autowired
    public TokenInputPasswordGrantFactory(TokenPayloadValidator tokenPayloadValidator) {
        this.tokenPayloadValidator = tokenPayloadValidator;
    }

    public TokenInputPasswordGrant run(Map<String, String> request) throws UnknownKeyException, InvalidValueException, MissingKeyException {

        if (request.size() > MAX_NUMBER_OF_KEYS) {
            Optional<String> unknownKey = tokenPayloadValidator.getFirstUnknownKey(request, KNOWN_KEYS);
            throw new UnknownKeyException(
                ErrorCode.UNKNOWN_KEY.getDescription(),
                unknownKey.get(),
                ErrorCode.UNKNOWN_KEY.getCode()
            );
        }

        String requestUsername = tokenPayloadValidator.required(request.get(USER_NAME), USER_NAME);
        String requestPassword = tokenPayloadValidator.required(request.get(PASSWORD), PASSWORD);
        Optional<String> requestScope = tokenPayloadValidator.optional(request.get(SCOPE), SCOPE);
        List<String> scopes = delimitedScopeToListScopes(requestScope);

        TokenInputPasswordGrant input = new TokenInputPasswordGrant();
        input.setUserName(requestUsername);
        input.setPassword(requestPassword);
        input.setScopes(scopes);

        return input;
    }

    protected List<String> delimitedScopeToListScopes(Optional<String> delimitedScopes) {
        List<String> scopes = new ArrayList<>();
        if (delimitedScopes.isPresent()) {
            scopes = Arrays.asList(delimitedScopes.get().split(" "))
                    .stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
        }
        return scopes;
    }
}
