package org.rootservices.authorization.oauth2.grant.password;

import org.rootservices.authorization.authenticate.LoginConfidentialClient;
import org.rootservices.authorization.authenticate.LoginResourceOwner;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.token.RequestTokenGrant;
import org.rootservices.authorization.oauth2.grant.password.entity.TokenInputPasswordGrant;
import org.rootservices.authorization.oauth2.grant.password.factory.TokenInputPasswordGrantFactory;
import org.rootservices.authorization.oauth2.grant.token.exception.BadRequestException;
import org.rootservices.authorization.oauth2.grant.token.exception.BadRequestExceptionBuilder;
import org.rootservices.authorization.oauth2.grant.token.exception.UnknownKeyException;
import org.rootservices.authorization.oauth2.grant.token.entity.Extension;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenResponse;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenType;
import org.rootservices.authorization.oauth2.grant.token.exception.InvalidValueException;
import org.rootservices.authorization.oauth2.grant.token.exception.MissingKeyException;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by tommackenzie on 9/18/16.
 */
@Component
public class RequestTokenPasswordGrant implements RequestTokenGrant {
    private LoginConfidentialClient loginConfidentialClient;
    private TokenInputPasswordGrantFactory tokenInputPasswordGrantFactory;
    private BadRequestExceptionBuilder badRequestExceptionBuilder;
    private LoginResourceOwner loginResourceOwner;
    private RandomString randomString;
    private IssueTokenPasswordGrant issueTokenPasswordGrant;

    private static String OPENID_SCOPE = "openid";

    @Autowired
    public RequestTokenPasswordGrant(LoginConfidentialClient loginConfidentialClient, TokenInputPasswordGrantFactory tokenInputPasswordGrantFactory, BadRequestExceptionBuilder badRequestExceptionBuilder, LoginResourceOwner loginResourceOwner, RandomString randomString, IssueTokenPasswordGrant issueTokenPasswordGrant) {
        this.loginConfidentialClient = loginConfidentialClient;
        this.tokenInputPasswordGrantFactory = tokenInputPasswordGrantFactory;
        this.badRequestExceptionBuilder = badRequestExceptionBuilder;
        this.loginResourceOwner = loginResourceOwner;
        this.randomString = randomString;
        this.issueTokenPasswordGrant = issueTokenPasswordGrant;
    }

    @Override
    public TokenResponse request(UUID clientId, String clientPassword, Map<String, String> request) throws BadRequestException, UnauthorizedException {

        ConfidentialClient confidentialClient = loginConfidentialClient.run(clientId, clientPassword);

        TokenInputPasswordGrant input;
        try {
            input = tokenInputPasswordGrantFactory.run(request);
        } catch (UnknownKeyException e) {
            throw badRequestExceptionBuilder.UnknownKey(e.getKey(), e.getCode(), e).build();
        } catch (InvalidValueException e) {
            throw badRequestExceptionBuilder.InvalidKeyValue(e.getKey(), e.getCode(), e).build();
        } catch (MissingKeyException e) {
            throw badRequestExceptionBuilder.MissingKey(e.getKey(), e).build();
        }

        ResourceOwner resourceOwner = loginResourceOwner.run(input.getUserName(), input.getPassword());

        List<Scope> scopes = matchScopes(confidentialClient.getClient().getScopes(), input.getScopes());

        String accessToken = randomString.run();
        Token token = issueTokenPasswordGrant.run(resourceOwner.getUuid(), accessToken, scopes);

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setExpiresIn(token.getSecondsToExpiration());
        tokenResponse.setTokenType(TokenType.BEARER);

        Extension extension = Extension.NONE;

        Boolean isOpenId = token.getTokenScopes().stream()
                .filter(o -> o.getScope().getName().equals(OPENID_SCOPE))
                .findFirst().isPresent();

        if (isOpenId) {
            extension = Extension.IDENTITY;
        }
        tokenResponse.setExtension(extension);

        return tokenResponse;
    }

    /**
     * Checks if each item in input scopes is in the scopes list. If a item in the input scopes is not
     * in the scopes list, then a BadRequestException is thrown.
     *
     * @param scopes
     * @param inputScope
     * @return a list of the matches
     * @throws BadRequestException
     */
    protected List<Scope> matchScopes(List<Scope> scopes, List<String> inputScope) throws BadRequestException {

        List<Scope> matches = new ArrayList<>();
        for(String scope: inputScope) {
            Optional<Scope> match = scopes.stream()
                    .filter(o -> o.getName().equals(scope))
                    .findFirst();

            if (!match.isPresent()) {
                throw badRequestExceptionBuilder.InvalidScope(ErrorCode.SCOPES_NOT_SUPPORTED.getCode()).build();
            }
            matches.add(match.get());
        }
        return matches;
    }
}
