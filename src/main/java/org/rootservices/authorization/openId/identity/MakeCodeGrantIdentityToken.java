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

    private static String RESOURCE_OWNER_NOT_FOUND = "resource owner was not found";
    private static String KEY_NOT_FOUND = "No key available to sign id token";
    private static String ALG_INVALID = "Algorithm to sign with is invalid";
    private static String KEY_INVALID = "key is invalid";
    private static String SERIALIZE_ERROR = "Could not serialize id token";

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
            throw new ResourceOwnerNotFoundException(RESOURCE_OWNER_NOT_FOUND, e);
        }

        if (ro.getProfile() == null) {
            throw new ProfileNotFoundException(RESOURCE_OWNER_NOT_FOUND);
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

        SecureJwtEncoder secureJwtEncoder;
        try {
            secureJwtEncoder = jwtAppFactory.secureJwtEncoder(Algorithm.RS256, rsaKeyPair);
        } catch (InvalidAlgorithmException e) {
            throw new IdTokenException(ALG_INVALID, e);
        } catch (InvalidJsonWebKeyException e) {
            throw new IdTokenException(KEY_INVALID, e);
        }

        String encodedJwt;
        try {
            encodedJwt = secureJwtEncoder.encode(idToken);
        } catch (JwtToJsonException e) {
            throw new IdTokenException(SERIALIZE_ERROR, e);
        }

        return encodedJwt;
    }
}
