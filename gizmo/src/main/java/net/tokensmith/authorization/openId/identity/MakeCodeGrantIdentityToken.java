package net.tokensmith.authorization.openId.identity;

import net.tokensmith.authorization.oauth2.grant.token.entity.TokenClaims;
import net.tokensmith.authorization.openId.identity.exception.*;
import net.tokensmith.authorization.openId.identity.entity.IdToken;
import net.tokensmith.authorization.openId.identity.factory.IdTokenFactory;
import net.tokensmith.authorization.openId.identity.translator.PrivateKeyTranslator;
import net.tokensmith.authorization.persistence.entity.RSAPrivateKey;
import net.tokensmith.authorization.persistence.entity.ResourceOwner;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;
import net.tokensmith.authorization.persistence.repository.ResourceOwnerRepository;
import net.tokensmith.authorization.persistence.repository.RsaPrivateKeyRepository;
import net.tokensmith.authorization.security.ciphers.HashToken;
import net.tokensmith.jwt.builder.compact.SecureCompactBuilder;
import net.tokensmith.jwt.builder.exception.CompactException;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.RSAKeyPair;
import net.tokensmith.jwt.entity.jwt.header.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by tommackenzie on 1/24/16.
 *
 * // add indexes.
 */
@Component
public class MakeCodeGrantIdentityToken {
    private HashToken hashToken;
    private ResourceOwnerRepository resourceOwnerRepository;
    private RsaPrivateKeyRepository rsaPrivateKeyRepository;
    private PrivateKeyTranslator privateKeyTranslator;
    private JwtAppFactory jwtAppFactory;
    private IdTokenFactory idTokenFactory;

    private static String RESOURCE_OWNER_NOT_FOUND = "resource owner was not found";
    private static String PROFILE_NOT_FOUND = "resource owner does not have a profile";
    private static String KEY_NOT_FOUND = "No key available to sign id token";
    private static String ID_TOKEN_ERROR_MSG = "Could not create id token";

    @Autowired
    public MakeCodeGrantIdentityToken(HashToken hashToken, ResourceOwnerRepository resourceOwnerRepository, RsaPrivateKeyRepository rsaPrivateKeyRepository, PrivateKeyTranslator privateKeyTranslator, JwtAppFactory jwtAppFactory, IdTokenFactory idTokenFactory) {
        this.hashToken = hashToken;
        this.resourceOwnerRepository = resourceOwnerRepository;
        this.rsaPrivateKeyRepository = rsaPrivateKeyRepository;
        this.privateKeyTranslator = privateKeyTranslator;
        this.jwtAppFactory = jwtAppFactory;
        this.idTokenFactory = idTokenFactory;
    }

    public String make(String accessToken, TokenClaims tokenClaims) throws IdTokenException, KeyNotFoundException, ProfileNotFoundException, ResourceOwnerNotFoundException {

        String hashedAccessToken = hashToken.run(accessToken);

        ResourceOwner ro;
        try {
            ro = resourceOwnerRepository.getByAccessTokenWithProfileAndTokens(hashedAccessToken);
        } catch (RecordNotFoundException e) {
            throw new ResourceOwnerNotFoundException(RESOURCE_OWNER_NOT_FOUND, e);
        }

        if (ro.getProfile() == null) {
            throw new ProfileNotFoundException(PROFILE_NOT_FOUND);
        }

        RSAPrivateKey key;
        try {
            key = rsaPrivateKeyRepository.getMostRecentAndActiveForSigning();
        } catch (RecordNotFoundException e) {
            throw new KeyNotFoundException(KEY_NOT_FOUND, e);
        }

        RSAKeyPair rsaKeyPair = privateKeyTranslator.from(key);

        // NPE - for ro.getTokens()
        List<String> scopesForIdToken = ro.getTokens().get(0).getTokenScopes().stream()
                .map(item -> item.getScope().getName())
                .collect(Collectors.toList());

        IdToken idToken = idTokenFactory.make(tokenClaims, scopesForIdToken, ro);

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
}
