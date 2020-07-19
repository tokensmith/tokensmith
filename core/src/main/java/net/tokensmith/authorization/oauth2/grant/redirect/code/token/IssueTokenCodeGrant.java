package net.tokensmith.authorization.oauth2.grant.redirect.code.token;

import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.exception.ServerException;
import net.tokensmith.authorization.oauth2.grant.redirect.code.token.exception.CompromisedCodeException;
import net.tokensmith.authorization.oauth2.grant.token.builder.TokenResponseBuilder;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenGraph;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenResponse;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenType;
import net.tokensmith.repository.entity.AuthCodeToken;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.repository.entity.ResourceOwnerToken;
import net.tokensmith.repository.entity.Scope;
import net.tokensmith.repository.entity.Token;
import net.tokensmith.repository.exceptions.DuplicateRecordException;
import net.tokensmith.repository.repo.AuthCodeRepository;
import net.tokensmith.repository.repo.AuthCodeTokenRepository;
import net.tokensmith.repository.repo.RefreshTokenRepository;
import net.tokensmith.repository.repo.ResourceOwnerTokenRepository;
import net.tokensmith.repository.repo.TokenRepository;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
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
    private String issuer;

    public IssueTokenCodeGrant(InsertTokenGraphCodeGrant insertTokenGraph, TokenRepository tokenRepository, RefreshTokenRepository refreshTokenRepository, AuthCodeTokenRepository authCodeTokenRepository, ResourceOwnerTokenRepository resourceOwnerTokenRepository, AuthCodeRepository authCodeRepository, String issuer) {
        this.insertTokenGraph = insertTokenGraph;
        this.tokenRepository = tokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.authCodeTokenRepository = authCodeTokenRepository;
        this.resourceOwnerTokenRepository = resourceOwnerTokenRepository;
        this.authCodeRepository = authCodeRepository;
        this.issuer = issuer;
    }

    public TokenResponse run(UUID clientId, UUID authCodeId, UUID resourceOwnerId, List<Scope> scopes, List<Client> audience, Optional<String> nonce) throws CompromisedCodeException, ServerException {

        TokenGraph tokenGraph = insertTokenGraph.insertTokenGraph(clientId, scopes, audience, nonce);
        relateTokenGraphToAuthCode(tokenGraph.getToken(), authCodeId, resourceOwnerId, clientId);

        List<String> responseAudience = tokenGraph.getToken().getAudience()
                .stream()
                .map(i->i.getId().toString())
                .collect(Collectors.toList());

        TokenResponse tr = new TokenResponseBuilder()
                .setAccessToken(tokenGraph.getPlainTextAccessToken())
                .setRefreshAccessToken(tokenGraph.getPlainTextRefreshToken().get())
                .setTokenType(TokenType.BEARER)
                .setExpiresIn(tokenGraph.getToken().getSecondsToExpiration())
                .setExtension(tokenGraph.getExtension())
                .setIssuer(issuer)
                .setAudience(responseAudience)
                .setIssuedAt(OffsetDateTime.now().toEpochSecond())
                .setExpirationTime(tokenGraph.getToken().getExpiresAt().toEpochSecond())
                .setAuthTime(tokenGraph.getToken().getCreatedAt().toEpochSecond())
                .nonce(nonce)
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
