package org.rootservices.authorization.oauth2.grant.password;

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
 * Created by tommackenzie on 9/18/16.
 */
@Component
public class IssueTokenPasswordGrant {
    private RandomString randomString;
    private MakeBearerToken makeBearerToken;
    private TokenRepository tokenRepository;
    private MakeRefreshToken makeRefreshToken;
    private RefreshTokenRepository refreshTokenRepository;
    private ResourceOwnerTokenRepository resourceOwnerTokenRepository;
    private TokenScopeRepository tokenScopeRepository;
    private ClientTokenRepository clientTokenRepository;
    private TokenResponseBuilder tokenResponseBuilder;

    private String issuer;
    private static String OPENID_SCOPE = "openid";

    @Autowired
    public IssueTokenPasswordGrant(RandomString randomString, MakeBearerToken makeBearerToken, TokenRepository tokenRepository, MakeRefreshToken makeRefreshToken, RefreshTokenRepository refreshTokenRepository, ResourceOwnerTokenRepository resourceOwnerTokenRepository, TokenScopeRepository tokenScopeRepository, ClientTokenRepository clientTokenRepository, TokenResponseBuilder tokenResponseBuilder, String issuer) {
        this.randomString = randomString;
        this.makeBearerToken = makeBearerToken;
        this.tokenRepository = tokenRepository;
        this.makeRefreshToken = makeRefreshToken;
        this.refreshTokenRepository = refreshTokenRepository;
        this.resourceOwnerTokenRepository = resourceOwnerTokenRepository;
        this.tokenScopeRepository = tokenScopeRepository;
        this.clientTokenRepository = clientTokenRepository;
        this.tokenResponseBuilder = tokenResponseBuilder;
        this.issuer = issuer;
    }

    public TokenResponse run(UUID clientId, UUID resourceOwnerId, String plainTextToken, List<Scope> scopes) {
        Token token = makeBearerToken.run(plainTextToken, 3600L);
        token.setGrantType(GrantType.PASSWORD);

        try {
            tokenRepository.insert(token);
        } catch( DuplicateRecordException e) {
            // TODO: handle this exception
        }

        String refreshAccessToken = randomString.run();
        RefreshToken refreshToken = makeRefreshToken.run(token, token, refreshAccessToken, 1209600L);

        try {
            refreshTokenRepository.insert(refreshToken);
        } catch (DuplicateRecordException e) {
            // TODO: handle this exception
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
        for(Scope scope: scopes) {
            TokenScope ts = new TokenScope();
            ts.setId(UUID.randomUUID());
            ts.setTokenId(token.getId());
            ts.setScope(scope);

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
                .setExpiresIn(token.getSecondsToExpiration())
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
