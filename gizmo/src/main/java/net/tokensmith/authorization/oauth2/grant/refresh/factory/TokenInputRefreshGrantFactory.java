package net.tokensmith.authorization.oauth2.grant.refresh.factory;

import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.oauth2.grant.refresh.entity.TokenInputRefreshGrant;
import net.tokensmith.authorization.oauth2.grant.token.exception.InvalidValueException;
import net.tokensmith.authorization.oauth2.grant.token.exception.MissingKeyException;
import net.tokensmith.authorization.oauth2.grant.token.exception.UnknownKeyException;
import net.tokensmith.authorization.oauth2.grant.token.validator.TokenPayloadValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by tommackenzie on 10/7/16.
 */
@Component
public class TokenInputRefreshGrantFactory {
    protected static String REFRESH_TOKEN = "refresh_token";
    protected static String SCOPE = "scope";
    private static List<String> KNOWN_KEYS = Arrays.asList("grant_type", "refresh_token", "scope");
    private TokenPayloadValidator tokenPayloadValidator;

    @Autowired
    public TokenInputRefreshGrantFactory(TokenPayloadValidator tokenPayloadValidator) {
        this.tokenPayloadValidator = tokenPayloadValidator;
    }

    public TokenInputRefreshGrant run(Map<String, String> request) throws UnknownKeyException, InvalidValueException, MissingKeyException {

        Optional<String> unknownKey = tokenPayloadValidator.getFirstUnknownKey(request, KNOWN_KEYS);
        if (unknownKey.isPresent()) {
            throw new UnknownKeyException(
                    ErrorCode.UNKNOWN_KEY.getDescription(),
                    unknownKey.get(),
                    ErrorCode.UNKNOWN_KEY.getCode()
            );
        }

        String requestRefreshToken = tokenPayloadValidator.required(request.get(REFRESH_TOKEN), REFRESH_TOKEN);
        Optional<String> requestScope = tokenPayloadValidator.optional(request.get(SCOPE), SCOPE);
        List<String> scopes = delimitedScopeToListScopes(requestScope);

        TokenInputRefreshGrant input = new TokenInputRefreshGrant();
        input.setRefreshToken(requestRefreshToken);
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
