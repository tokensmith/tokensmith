package org.rootservices.authorization.oauth2.grant.redirect.code.token;

import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.exception.CompromisedCodeException;
import org.rootservices.authorization.oauth2.grant.token.MakeBearerToken;
import org.rootservices.authorization.oauth2.grant.token.MakeRefreshToken;
import org.rootservices.authorization.oauth2.grant.token.builder.TokenResponseBuilder;
import org.rootservices.authorization.oauth2.grant.token.entity.Extension;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenResponse;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenType;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.*;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 8/28/16.
 */
@Component
public class IssueTokenCodeGrant {
    private RandomString randomString;
    private MakeBearerToken makeBearerToken;
    private TokenRepository tokenRepository;
    private MakeRefreshToken makeRefreshToken;
    private RefreshTokenRepository refreshTokenRepository;
    private AuthCodeTokenRepository authCodeTokenRepository;
    private ResourceOwnerTokenRepository resourceOwnerTokenRepository;
    private TokenScopeRepository tokenScopeRepository;
    private AuthCodeRepository authCodeRepository;
    private ClientTokenRepository clientTokenRepository;
    private TokenResponseBuilder tokenResponseBuilder;

    private String issuer;
    private static String OPENID_SCOPE = "openid";

    @Autowired
    public IssueTokenCodeGrant(RandomString randomString, MakeBearerToken makeBearerToken, TokenRepository tokenRepository, MakeRefreshToken makeRefreshToken, RefreshTokenRepository refreshTokenRepository, AuthCodeTokenRepository authCodeTokenRepository, ResourceOwnerTokenRepository resourceOwnerTokenRepository, TokenScopeRepository tokenScopeRepository, AuthCodeRepository authCodeRepository, ClientTokenRepository clientTokenRepository, TokenResponseBuilder tokenResponseBuilder, String issuer) {
        this.randomString = randomString;
        this.makeBearerToken = makeBearerToken;
        this.tokenRepository = tokenRepository;
        this.makeRefreshToken = makeRefreshToken;
        this.refreshTokenRepository = refreshTokenRepository;
        this.authCodeTokenRepository = authCodeTokenRepository;
        this.resourceOwnerTokenRepository = resourceOwnerTokenRepository;
        this.tokenScopeRepository = tokenScopeRepository;
        this.authCodeRepository = authCodeRepository;
        this.clientTokenRepository = clientTokenRepository;
        this.tokenResponseBuilder = tokenResponseBuilder;
        this.issuer = issuer;
    }

    public TokenResponse run(UUID clientId, UUID authCodeId, UUID resourceOwnerId, String plainTextToken, List<AccessRequestScope> accessRequestScopes) throws CompromisedCodeException {
        Token token = makeBearerToken.run(plainTextToken);

        try {
            tokenRepository.insert(token);
        } catch( DuplicateRecordException e) {
            // TODO: handle this exception
        }

        String refreshAccessToken = randomString.run();
        RefreshToken refreshToken = makeRefreshToken.run(token, token, refreshAccessToken);

        try {
            refreshTokenRepository.insert(refreshToken);
        } catch (DuplicateRecordException e) {
            // TODO: handle this exception
        }

        try {
            AuthCodeToken authCodeToken = new AuthCodeToken();
            authCodeToken.setId(UUID.randomUUID());
            authCodeToken.setTokenId(token.getId());
            authCodeToken.setAuthCodeId(authCodeId);

            authCodeTokenRepository.insert(authCodeToken);
        } catch (DuplicateRecordException e) {
            tokenRepository.revokeByAuthCodeId(authCodeId);
            authCodeRepository.revokeById(authCodeId);
            refreshTokenRepository.revokeByAuthCodeId(authCodeId);

            tokenRepository.revokeById(token.getId());
            refreshTokenRepository.revokeByTokenId(token.getId());

            throw new CompromisedCodeException(
                    ErrorCode.COMPROMISED_AUTH_CODE.getDescription(),
                    "invalid_grant", e, ErrorCode.COMPROMISED_AUTH_CODE.getCode()
            );
        }

        ResourceOwner resourceOwner = new ResourceOwner();
        resourceOwner.setId(resourceOwnerId);

        ResourceOwnerToken resourceOwnerToken = new ResourceOwnerToken();
        resourceOwnerToken.setId(UUID.randomUUID());
        resourceOwnerToken.setResourceOwner(resourceOwner);
        resourceOwnerToken.setToken(token);

        resourceOwnerTokenRepository.insert(resourceOwnerToken);

        ClientToken clientToken = new ClientToken();
        clientToken.setId(UUID.randomUUID());
        clientToken.setClientId(clientId);
        clientToken.setTokenId(token.getId());

        clientTokenRepository.insert(clientToken);

        Extension extension = Extension.NONE;
        for(AccessRequestScope ars: accessRequestScopes) {
            TokenScope ts = new TokenScope();
            ts.setId(UUID.randomUUID());
            ts.setTokenId(token.getId());
            ts.setScope(ars.getScope());

            if (OPENID_SCOPE.equalsIgnoreCase(ts.getScope().getName())) {
                extension = Extension.IDENTITY;
            }

            tokenScopeRepository.insert(ts);
        }

        // build the response.
        List<String> audience = new ArrayList<>();
        audience.add(clientId.toString());

        TokenResponse tr = tokenResponseBuilder
                .setAccessToken(plainTextToken)
                .setRefreshAccessToken(refreshAccessToken)
                .setTokenType(TokenType.BEARER)
                .setExpiresIn(makeBearerToken.getSecondsToExpiration())
                .setExtension(extension)
                .setIssuer(issuer)
                .setAudience(audience)
                .setIssuedAt(OffsetDateTime.now().toEpochSecond())
                .setExpirationTime(token.getExpiresAt().toEpochSecond())
                .setAuthTime(token.getCreatedAt().toEpochSecond())
                .build();

        return tr;
    }
}
