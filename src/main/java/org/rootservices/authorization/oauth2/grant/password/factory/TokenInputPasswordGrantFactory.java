package org.rootservices.authorization.oauth2.grant.password.factory;

import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.password.entity.TokenInputPasswordGrant;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.factory.exception.UnknownKeyException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.validator.exception.InvalidValueException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.validator.exception.MissingKeyException;
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
    private static String MISSING_KEY_MSG = "missing key ";

    public TokenInputPasswordGrantFactory() {
    }

    public TokenInputPasswordGrant run(Map<String, String> request) throws UnknownKeyException, InvalidValueException, MissingKeyException {

        if (request.size() > MAX_NUMBER_OF_KEYS) {
            throw new UnknownKeyException(ErrorCode.UNKNOWN_KEY.getDescription(), ErrorCode.UNKNOWN_KEY.getCode());
        }

        String requestUsername = required(request.get(USER_NAME), USER_NAME);
        String requestPassword = required(request.get(PASSWORD), PASSWORD);
        Optional<String> requestScope = optional(request.get(SCOPE), SCOPE);

        List<String> scopes = new ArrayList<>();
        if (requestScope.isPresent()) {
            scopes = Arrays.asList(request.get(SCOPE).split(" "))
                    .stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
        }

        TokenInputPasswordGrant input = new TokenInputPasswordGrant();
        input.setUserName(requestUsername);
        input.setPassword(requestPassword);
        input.setScopes(scopes);

        return input;
    }

    protected String required(String input, String key) throws MissingKeyException{
        if (input == null || input.isEmpty()){
            throw new MissingKeyException(MISSING_KEY_MSG + key, key);
        }
        return input;
    }

    protected Optional<String> optional(String input, String key) throws InvalidValueException {

        Optional<String> output = Optional.empty();
        if (input == null || input.isEmpty()){
            throw new InvalidValueException(
                ErrorCode.SCOPES_EMPTY_VALUE.getDescription(),
                ErrorCode.SCOPES_EMPTY_VALUE.getCode(),
                key,
                input
            );
        }

        if (input != null) {
            output = Optional.of(input);
        }
        return output;
    }
}
