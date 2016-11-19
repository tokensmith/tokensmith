package org.rootservices.authorization.oauth2.grant.redirect.code.token;

import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.exception.ServerException;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenGraph;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.exception.CompromisedCodeException;
import org.rootservices.authorization.oauth2.grant.token.builder.TokenResponseBuilder;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenResponse;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenType;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.*;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by tommackenzie on 8/28/16.
 */
@Component
public class IssueTokenCodeGrant {

    private InsertTokenGraphCodeGrant insertTokenGraph;
    private TokenRepository tokenRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private AuthCodeTokenRepository authCodeTokenRepository;
    private ResourceOwnerTokenRepository resourceOwnerTokenRepository;
    private AuthCodeRepository authCodeRepository;
    private ClientTokenRepository clientTokenRepository;
    private TokenResponseBuilder tokenResponseBuilder;
    private String issuer;

    public IssueTokenCodeGrant(InsertTokenGraphCodeGrant insertTokenGraph, TokenRepository tokenRepository, RefreshTokenRepository refreshTokenRepository, AuthCodeTokenRepository authCodeTokenRepository, ResourceOwnerTokenRepository resourceOwnerTokenRepository, AuthCodeRepository authCodeRepository, ClientTokenRepository clientTokenRepository, TokenResponseBuilder tokenResponseBuilder, String issuer) {
        this.insertTokenGraph = insertTokenGraph;
        this.tokenRepository = tokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.authCodeTokenRepository = authCodeTokenRepository;
        this.resourceOwnerTokenRepository = resourceOwnerTokenRepository;
        this.authCodeRepository = authCodeRepository;
        this.clientTokenRepository = clientTokenRepository;
        this.tokenResponseBuilder = tokenResponseBuilder;
        this.issuer = issuer;
    }

    public TokenResponse run(UUID clientId, UUID authCodeId, UUID resourceOwnerId, List<Scope> scopes) throws CompromisedCodeException, ServerException {
        TokenGraph tokenGraph = insertTokenGraph.insertTokenGraph(scopes);
        relateTokenGraphToAuthCode(tokenGraph.getToken(), authCodeId, resourceOwnerId, clientId);

        List<String> audience = new ArrayList<>();
        audience.add(clientId.toString());

        TokenResponse tr = tokenResponseBuilder
                .setAccessToken(tokenGraph.getPlainTextAccessToken())
                .setRefreshAccessToken(tokenGraph.getPlainTextRefreshToken().get())
                .setTokenType(TokenType.BEARER)
                .setExpiresIn(tokenGraph.getToken().getSecondsToExpiration())
                .setExtension(tokenGraph.getExtension())
                .setIssuer(issuer)
                .setAudience(audience)
                .setIssuedAt(OffsetDateTime.now().toEpochSecond())
                .setExpirationTime(tokenGraph.getToken().getExpiresAt().toEpochSecond())
                .setAuthTime(tokenGraph.getToken().getCreatedAt().toEpochSecond())
                .build();

        return tr;
    }

    protected void relateTokenGraphToAuthCode(Token token, UUID authCodeId, UUID resourceOwnerId, UUID clientId) throws CompromisedCodeException {
        // insert auth_code_token
        AuthCodeToken authCodeToken = makeAuthCodeToken(token.getId(), authCodeId);
        try {
            authCodeTokenRepository.insert(authCodeToken);
        } catch (DuplicateRecordException e) {
            handleDuplicateAuthCodeToken(e, authCodeId, token.getId());
        }

        // insert resource_owner_token. Associate token with resource owner.
        ResourceOwnerToken resourceOwnerToken = makeResourceOwnerToken(resourceOwnerId, token);
        resourceOwnerTokenRepository.insert(resourceOwnerToken);

        // insert client_token. Associate token with client.
        ClientToken clientToken = makeClientToken(clientId, token.getId());
        clientTokenRepository.insert(clientToken);
    }

    protected AuthCodeToken makeAuthCodeToken(UUID tokenId, UUID authCodeId) {
        AuthCodeToken authCodeToken = new AuthCodeToken();
        authCodeToken.setId(UUID.randomUUID());
        authCodeToken.setTokenId(tokenId);
        authCodeToken.setAuthCodeId(authCodeId);
        return authCodeToken;
    }

    protected  ResourceOwnerToken makeResourceOwnerToken(UUID resourceOwnerId, Token token) {
        ResourceOwner resourceOwner = new ResourceOwner();
        resourceOwner.setId(resourceOwnerId);

        ResourceOwnerToken resourceOwnerToken = new ResourceOwnerToken();
        resourceOwnerToken.setId(UUID.randomUUID());
        resourceOwnerToken.setResourceOwner(resourceOwner);
        resourceOwnerToken.setToken(token);
        return resourceOwnerToken;
    }

    protected ClientToken makeClientToken(UUID clientId, UUID tokenId) {
        ClientToken clientToken = new ClientToken();
        clientToken.setId(UUID.randomUUID());
        clientToken.setClientId(clientId);
        clientToken.setTokenId(tokenId);
        return clientToken;
    }

    protected void handleDuplicateAuthCodeToken(DuplicateRecordException e, UUID authCodeId, UUID tokenId) throws CompromisedCodeException {
        tokenRepository.revokeByAuthCodeId(authCodeId);
        authCodeRepository.revokeById(authCodeId);
        refreshTokenRepository.revokeByAuthCodeId(authCodeId);

        tokenRepository.revokeById(tokenId);
        refreshTokenRepository.revokeByTokenId(tokenId);

        throw new CompromisedCodeException(
            ErrorCode.COMPROMISED_AUTH_CODE.getDescription(),
            "invalid_grant",
            e,
            ErrorCode.COMPROMISED_AUTH_CODE.getCode()
        );
    }
}
