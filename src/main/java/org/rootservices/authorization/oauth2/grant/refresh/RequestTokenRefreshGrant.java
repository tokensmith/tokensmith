package org.rootservices.authorization.oauth2.grant.refresh;

import org.rootservices.authorization.authenticate.LoginConfidentialClient;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.refresh.entity.TokenInputRefreshGrant;
import org.rootservices.authorization.oauth2.grant.refresh.exception.CompromisedRefreshTokenException;
import org.rootservices.authorization.oauth2.grant.refresh.factory.TokenInputRefreshGrantFactory;
import org.rootservices.authorization.oauth2.grant.token.RequestTokenGrant;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenResponse;
import org.rootservices.authorization.oauth2.grant.token.exception.*;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.RefreshTokenRepository;
import org.rootservices.authorization.persistence.repository.ResourceOwnerTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by tommackenzie on 10/8/16.
 */
@Component
public class RequestTokenRefreshGrant implements RequestTokenGrant {
    private LoginConfidentialClient loginConfidentialClient;
    private TokenInputRefreshGrantFactory tokenInputRefreshGrantFactory;
    private BadRequestExceptionBuilder badRequestExceptionBuilder;
    private RefreshTokenRepository refreshTokenRepository;
    private ResourceOwnerTokenRepository resourceOwnerTokenRepository;
    private IssueTokenRefreshGrant issueTokenRefreshGrant;

    private static String INVALID_GRANT = "invalid_grant";
    private static String REFRESH_TOKEN_NOT_FOUND = "refresh token was not found";
    private static String RESOURCE_OWNER_NOT_FOUND = "no resource owner was associated to refresh token";

    @Autowired
    public RequestTokenRefreshGrant(LoginConfidentialClient loginConfidentialClient, TokenInputRefreshGrantFactory tokenInputRefreshGrantFactory, BadRequestExceptionBuilder badRequestExceptionBuilder, RefreshTokenRepository refreshTokenRepository, ResourceOwnerTokenRepository resourceOwnerTokenRepository, IssueTokenRefreshGrant issueTokenRefreshGrant) {
        this.loginConfidentialClient = loginConfidentialClient;
        this.tokenInputRefreshGrantFactory = tokenInputRefreshGrantFactory;
        this.badRequestExceptionBuilder = badRequestExceptionBuilder;
        this.refreshTokenRepository = refreshTokenRepository;
        this.resourceOwnerTokenRepository = resourceOwnerTokenRepository;
        this.issueTokenRefreshGrant = issueTokenRefreshGrant;
    }

    @Override
    public TokenResponse request(UUID clientId, String clientPassword, Map<String, String> request) throws BadRequestException, NotFoundException, UnauthorizedException {
        // login in a confidential client.
        ConfidentialClient cc = loginConfidentialClient.run(clientId, clientPassword);

        TokenInputRefreshGrant input;
        try {
            input = tokenInputRefreshGrantFactory.run(request);
        } catch (MissingKeyException e) {
            throw badRequestExceptionBuilder.MissingKey(e.getKey(), e).build();
        } catch (InvalidValueException e) {
            throw badRequestExceptionBuilder.InvalidKeyValue(e.getKey(), e.getCode(), e).build();
        } catch (UnknownKeyException e) {
            throw badRequestExceptionBuilder.UnknownKey(e.getKey(), e.getCode(), e).build();
        }

        RefreshToken refreshToken = getRefreshToken(cc.getClient().getId(), input.getRefreshToken());
        List<Scope> scopes = matchScopes(input.getScopes(), refreshToken.getToken().getTokenScopes());

        String accessToken = new String(refreshToken.getToken().getToken());
        UUID resourceOwnerId = getResourceOwnerId(accessToken);

        TokenResponse tokenResponse = null;
        try {
            tokenResponse = issueTokenRefreshGrant.run(
                cc.getClient().getId(),
                resourceOwnerId,
                refreshToken.getTokenId(),
                refreshToken.getId(),
                scopes
            );
        } catch (CompromisedRefreshTokenException e) {
            // TODO:
            throw badRequestExceptionBuilder.CompromisedRefreshToken(
                ErrorCode.COMPROMISED_REFRESH_TOKEN.getCode(),
                e
            ).build();
        }

        return tokenResponse;
    }

    protected RefreshToken getRefreshToken(UUID clientId, String token) throws NotFoundException {
        RefreshToken refreshToken;

        try {
            refreshToken = refreshTokenRepository.getByClientIdAndAccessToken(clientId, token);
        } catch (RecordNotFoundException e) {
            throw new NotFoundException(
                REFRESH_TOKEN_NOT_FOUND,
                INVALID_GRANT,
                ErrorCode.REFRESH_TOKEN_NOT_FOUND.getDescription(),
                ErrorCode.REFRESH_TOKEN_NOT_FOUND.getCode(),
                e
            );
        }
        return refreshToken;
    }

    protected List<Scope> matchScopes(List<String> inputScopes, List<TokenScope> tokenScopes) throws BadRequestException {

        List<Scope> matchedScopes;

        if (inputScopes.size() > 0) {
            matchedScopes = tokenScopes.stream()
                    .map(item -> item.getScope())
                    .filter(item -> inputScopes.contains(item.getName()))
                    .collect(Collectors.toList());

            // matched scopes should be the same size as inputScopes.
            if (matchedScopes.size() != inputScopes.size()) {
                throw badRequestExceptionBuilder.InvalidScope(ErrorCode.SCOPES_NOT_SUPPORTED.getCode()).build();
            }
        } else {
            matchedScopes = tokenScopes.stream()
                    .map(item -> item.getScope())
                    .collect(Collectors.toList());
        }

        return matchedScopes;
    }

    protected UUID getResourceOwnerId(String accessToken) throws NotFoundException {
        ResourceOwnerToken rot;
        try {
            rot = resourceOwnerTokenRepository.getByAccessToken(accessToken);
        } catch (RecordNotFoundException e) {
            // TODO: should this have different desc and code?
            throw new NotFoundException(
                RESOURCE_OWNER_NOT_FOUND,
                INVALID_GRANT,
                ErrorCode.REFRESH_TOKEN_NOT_FOUND.getDescription(),
                ErrorCode.REFRESH_TOKEN_NOT_FOUND.getCode(),
                e
            );
        }

        return rot.getResourceOwner().getId();
    }
}
