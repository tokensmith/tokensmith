package org.rootservices.authorization.openId.identity;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenClaims;
import org.rootservices.authorization.openId.identity.entity.IdToken;
import org.rootservices.authorization.openId.identity.exception.IdTokenException;
import org.rootservices.authorization.openId.identity.exception.KeyNotFoundException;
import org.rootservices.authorization.openId.identity.exception.ResourceOwnerNotFoundException;
import org.rootservices.authorization.openId.identity.factory.IdTokenFactory;
import org.rootservices.authorization.openId.identity.translator.PrivateKeyTranslator;
import org.rootservices.authorization.persistence.entity.RSAPrivateKey;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ResourceOwnerRepository;
import org.rootservices.authorization.persistence.repository.RsaPrivateKeyRepository;
import org.rootservices.authorization.security.ciphers.HashTextStaticSalt;
import org.rootservices.jwt.builder.compact.SecureCompactBuilder;
import org.rootservices.jwt.builder.exception.CompactException;
import org.rootservices.jwt.config.JwtAppFactory;
import org.rootservices.jwt.entity.jwk.RSAKeyPair;
import org.rootservices.jwt.entity.jwt.header.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by tommackenzie on 1/24/16.
 */
@Component
public class MakeUserInfoIdentityToken {
    private static final Logger logger = LogManager.getLogger(MakeUserInfoIdentityToken.class);

    private HashTextStaticSalt hashText;
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
    public MakeUserInfoIdentityToken(HashTextStaticSalt hashText, ResourceOwnerRepository resourceOwnerRepository, RsaPrivateKeyRepository rsaPrivateKeyRepository, PrivateKeyTranslator privateKeyTranslator, JwtAppFactory jwtAppFactory, IdTokenFactory idTokenFactory, String issuer) {
        this.hashText = hashText;
        this.resourceOwnerRepository = resourceOwnerRepository;
        this.rsaPrivateKeyRepository = rsaPrivateKeyRepository;
        this.privateKeyTranslator = privateKeyTranslator;
        this.jwtAppFactory = jwtAppFactory;
        this.idTokenFactory = idTokenFactory;
        this.issuer = issuer;
    }


    public String make(String accessToken) throws IdTokenException, KeyNotFoundException, ResourceOwnerNotFoundException {
        String hashedAccessToken = hashText.run(accessToken);

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

        String encodedJwt;
        SecureCompactBuilder compactBuilder = new SecureCompactBuilder();
        try {
            encodedJwt = compactBuilder.alg(Algorithm.RS256)
                    .key(rsaKeyPair)
                    .claims(idToken)
                    .build().toString();
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
