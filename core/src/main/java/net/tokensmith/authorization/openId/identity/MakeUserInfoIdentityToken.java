package net.tokensmith.authorization.openId.identity;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenClaims;
import net.tokensmith.authorization.openId.identity.entity.IdToken;
import net.tokensmith.authorization.openId.identity.exception.IdTokenException;
import net.tokensmith.authorization.openId.identity.exception.KeyNotFoundException;
import net.tokensmith.authorization.openId.identity.exception.ResourceOwnerNotFoundException;
import net.tokensmith.authorization.openId.identity.factory.IdTokenFactory;
import net.tokensmith.authorization.openId.identity.translator.PrivateKeyTranslator;
import net.tokensmith.repository.entity.RSAPrivateKey;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.repository.entity.Token;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.ResourceOwnerRepository;
import net.tokensmith.repository.repo.RsaPrivateKeyRepository;
import net.tokensmith.authorization.security.ciphers.HashToken;
import net.tokensmith.jwt.builder.compact.SecureCompactBuilder;
import net.tokensmith.jwt.builder.exception.CompactException;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.RSAKeyPair;
import net.tokensmith.jwt.entity.jwt.header.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by tommackenzie on 1/24/16.
 */
@Component
public class MakeUserInfoIdentityToken {
    private static final Logger logger = LoggerFactory.getLogger(MakeUserInfoIdentityToken.class);

    private HashToken hashToken;
    private ResourceOwnerRepository resourceOwnerRepository;
    private RsaPrivateKeyRepository rsaPrivateKeyRepository;
    private PrivateKeyTranslator privateKeyTranslator;
    private JwtAppFactory jwtAppFactory;
    private IdTokenFactory idTokenFactory;
    private String issuer;

    private static String RESOURCE_OWNER_NOT_FOUND = "resource owner was not found";
    private static String KEY_NOT_FOUND = "No key available to sign id token";
    private static String ID_TOKEN_ERROR_MSG = "Could not create id token";

    @Autowired
    public MakeUserInfoIdentityToken(HashToken hashToken, ResourceOwnerRepository resourceOwnerRepository, RsaPrivateKeyRepository rsaPrivateKeyRepository, PrivateKeyTranslator privateKeyTranslator, JwtAppFactory jwtAppFactory, IdTokenFactory idTokenFactory, String issuer) {
        this.hashToken = hashToken;
        this.resourceOwnerRepository = resourceOwnerRepository;
        this.rsaPrivateKeyRepository = rsaPrivateKeyRepository;
        this.privateKeyTranslator = privateKeyTranslator;
        this.jwtAppFactory = jwtAppFactory;
        this.idTokenFactory = idTokenFactory;
        this.issuer = issuer;
    }


    public ByteArrayOutputStream make(String accessToken) throws IdTokenException, KeyNotFoundException, ResourceOwnerNotFoundException {
        String hashedAccessToken = hashToken.run(accessToken);

        ResourceOwner ro;
        try {
            ro = resourceOwnerRepository.getByAccessTokenWithProfileAndTokens(hashedAccessToken);
        } catch (RecordNotFoundException e) {
            throw new ResourceOwnerNotFoundException(RESOURCE_OWNER_NOT_FOUND, e);
        }

        RSAPrivateKey key;
        try {
            key = rsaPrivateKeyRepository.getMostRecentAndActiveForSigning();
        } catch (RecordNotFoundException e) {
            throw new KeyNotFoundException(KEY_NOT_FOUND, e);
        }

        RSAKeyPair rsaKeyPair = privateKeyTranslator.from(key);

        TokenClaims tc = makeTokenClaims(ro.getTokens().get(0));

        /**
         * assumption is that ro.getTokens() will not throw NPE since
         *  - resourceOwnerRepository.getByAccessTokenWithProfileAndTokens must return a token.
         *  - this is called after a token is issued.
         */
        List<String> scopesForIdToken = ro.getTokens().get(0).getTokenScopes().stream()
                .map(item -> item.getScope().getName())
                .collect(Collectors.toList());

        /*
         TODO: remove Token Claims and rely on token.
         https://www.pivotaltracker.com/story/show/136181907
          */
        IdToken idToken = idTokenFactory.make(tc, scopesForIdToken, ro);

        ByteArrayOutputStream encodedJwt;
        SecureCompactBuilder compactBuilder = new SecureCompactBuilder();
        try {
            encodedJwt = compactBuilder.alg(Algorithm.RS256)
                    .key(rsaKeyPair)
                    .claims(idToken)
                    .build();
        } catch (CompactException e) {
            throw new IdTokenException(ID_TOKEN_ERROR_MSG, e);
        }
        return encodedJwt;
    }

    protected TokenClaims makeTokenClaims(Token token) {
        List<String> audience = token.getAudience().stream()
                .map(i->i.getId().toString())
                .collect(Collectors.toList());

        TokenClaims tc = new TokenClaims();
        tc.setAudience(audience);
        tc.setIssuer(issuer);
        tc.setExpirationTime(token.getExpiresAt().toEpochSecond());
        tc.setIssuedAt(token.getCreatedAt().toEpochSecond());

        if (token.getLeadToken() != null) {
            tc.setAuthTime(token.getLeadToken().getCreatedAt().toEpochSecond());
        } else {
            tc.setAuthTime(token.getCreatedAt().toEpochSecond());
        }
        return tc;
    }
}
