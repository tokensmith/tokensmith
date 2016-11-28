package org.rootservices.authorization.openId.identity;

import org.rootservices.authorization.oauth2.grant.token.entity.TokenClaims;
import org.rootservices.authorization.openId.identity.exception.*;
import org.rootservices.authorization.openId.identity.entity.IdToken;
import org.rootservices.authorization.openId.identity.factory.IdTokenFactory;
import org.rootservices.authorization.openId.identity.translator.PrivateKeyTranslator;
import org.rootservices.authorization.persistence.entity.Profile;
import org.rootservices.authorization.persistence.entity.RSAPrivateKey;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.entity.ResourceOwnerToken;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ProfileRepository;
import org.rootservices.authorization.persistence.repository.ResourceOwnerRepository;
import org.rootservices.authorization.persistence.repository.ResourceOwnerTokenRepository;
import org.rootservices.authorization.persistence.repository.RsaPrivateKeyRepository;
import org.rootservices.authorization.security.HashTextStaticSalt;
import org.rootservices.jwt.SecureJwtEncoder;
import org.rootservices.jwt.config.AppFactory;
import org.rootservices.jwt.entity.jwk.RSAKeyPair;
import org.rootservices.jwt.entity.jwt.header.Algorithm;
import org.rootservices.jwt.serializer.exception.JwtToJsonException;
import org.rootservices.jwt.signature.signer.factory.exception.InvalidAlgorithmException;
import org.rootservices.jwt.signature.signer.factory.exception.InvalidJsonWebKeyException;
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
    private HashTextStaticSalt hashText;
    private ResourceOwnerRepository resourceOwnerRepository;
    private RsaPrivateKeyRepository rsaPrivateKeyRepository;
    private PrivateKeyTranslator privateKeyTranslator;
    private AppFactory jwtAppFactory;
    private IdTokenFactory idTokenFactory;

    @Autowired
    public MakeCodeGrantIdentityToken(HashTextStaticSalt hashText, ResourceOwnerRepository resourceOwnerRepository, RsaPrivateKeyRepository rsaPrivateKeyRepository, PrivateKeyTranslator privateKeyTranslator, AppFactory jwtAppFactory, IdTokenFactory idTokenFactory) {
        this.hashText = hashText;
        this.resourceOwnerRepository = resourceOwnerRepository;
        this.rsaPrivateKeyRepository = rsaPrivateKeyRepository;
        this.privateKeyTranslator = privateKeyTranslator;
        this.jwtAppFactory = jwtAppFactory;
        this.idTokenFactory = idTokenFactory;
    }

    public String make(String accessToken, TokenClaims tokenClaims) throws IdTokenException, KeyNotFoundException, ProfileNotFoundException, ResourceOwnerNotFoundException {

        String hashedAccessToken = hashText.run(accessToken);

        ResourceOwner ro;
        try {
            ro = resourceOwnerRepository.getByAccessTokenWithProfileAndTokens(hashedAccessToken);
        } catch (RecordNotFoundException e) {
            throw new ResourceOwnerNotFoundException("resource owner was not found", e);
        }

        if (ro.getProfile() == null) {
            throw new ProfileNotFoundException("resource owner was not found");
        }

        RSAPrivateKey key = null;
        try {
            key = rsaPrivateKeyRepository.getMostRecentAndActiveForSigning();
        } catch (RecordNotFoundException e) {
            throw new KeyNotFoundException("No key available to sign id token", e);
        }

        RSAKeyPair rsaKeyPair = privateKeyTranslator.from(key);

        // NPE - for ro.getTokens()
        List<String> scopesForIdToken = ro.getTokens().get(0).getTokenScopes().stream()
                .map(item -> item.getScope().getName())
                .collect(Collectors.toList());

        // NPE - ro.getProfile()
        IdToken idToken = idTokenFactory.make(tokenClaims, scopesForIdToken, ro.getProfile());

        SecureJwtEncoder secureJwtEncoder;
        try {
            secureJwtEncoder = jwtAppFactory.secureJwtEncoder(Algorithm.RS256, rsaKeyPair);
        } catch (InvalidAlgorithmException e) {
            throw new IdTokenException("Algorithm to sign with is invalid", e);
        } catch (InvalidJsonWebKeyException e) {
            throw new IdTokenException("key is invalid", e);
        }

        String encodedJwt = null;
        try {
            encodedJwt = secureJwtEncoder.encode(idToken);
        } catch (JwtToJsonException e) {
            throw new IdTokenException("Could not serialize id token", e);
        }

        return encodedJwt;
    }
}
