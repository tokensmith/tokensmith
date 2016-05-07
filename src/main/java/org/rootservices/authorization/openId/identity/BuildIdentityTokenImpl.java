package org.rootservices.authorization.openId.identity;

import org.rootservices.authorization.openId.identity.exception.IdTokenException;
import org.rootservices.authorization.openId.identity.exception.KeyNotFoundException;
import org.rootservices.authorization.openId.identity.exception.ProfileNotFoundException;
import org.rootservices.authorization.openId.identity.exception.AccessRequestNotFoundException;
import org.rootservices.authorization.openId.identity.factory.IdTokenFactory;
import org.rootservices.authorization.openId.identity.entity.IdToken;
import org.rootservices.authorization.openId.identity.translator.PrivateKeyTranslator;
import org.rootservices.authorization.persistence.entity.Profile;
import org.rootservices.authorization.persistence.entity.RSAPrivateKey;
import org.rootservices.authorization.persistence.entity.ResourceOwnerToken;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ProfileRepository;
import org.rootservices.authorization.persistence.repository.ResourceOwnerTokenRepository;
import org.rootservices.authorization.persistence.repository.RsaPrivateKeyRepository;
import org.rootservices.authorization.security.HashTextStaticSalt;
import org.rootservices.jwt.builder.SecureJwtBuilder;
import org.rootservices.jwt.config.AppFactory;
import org.rootservices.jwt.entity.jwk.RSAKeyPair;
import org.rootservices.jwt.entity.jwt.JsonWebToken;
import org.rootservices.jwt.entity.jwt.header.Algorithm;
import org.rootservices.jwt.serializer.JWTSerializer;
import org.rootservices.jwt.serializer.exception.JwtToJsonException;
import org.rootservices.jwt.signature.signer.factory.exception.InvalidAlgorithmException;
import org.rootservices.jwt.signature.signer.factory.exception.InvalidJsonWebKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 1/24/16.
 *
 * // add indexes.
 */
@Component
public class BuildIdentityTokenImpl implements BuildIdentityToken {
    private HashTextStaticSalt hashText;
    private RsaPrivateKeyRepository rsaPrivateKeyRepository;
    private ResourceOwnerTokenRepository resourceOwnerTokenRepository;
    private PrivateKeyTranslator privateKeyTranslator;
    private AppFactory jwtAppFactory;
    private ProfileRepository profileRepository;
    private IdTokenFactory idTokenFactory;

    @Autowired
    public BuildIdentityTokenImpl(HashTextStaticSalt hashText, RsaPrivateKeyRepository rsaPrivateKeyRepository, ResourceOwnerTokenRepository resourceOwnerTokenRepository, PrivateKeyTranslator privateKeyTranslator, AppFactory jwtAppFactory, ProfileRepository profileRepository, IdTokenFactory idTokenFactory) {
        this.hashText = hashText;
        this.rsaPrivateKeyRepository = rsaPrivateKeyRepository;
        this.resourceOwnerTokenRepository = resourceOwnerTokenRepository;
        this.privateKeyTranslator = privateKeyTranslator;
        this.jwtAppFactory = jwtAppFactory;
        this.profileRepository = profileRepository;
        this.idTokenFactory = idTokenFactory;
    }

    @Override
    public String build(String accessToken) throws AccessRequestNotFoundException, IdTokenException, KeyNotFoundException, ProfileNotFoundException {

        String hashedAccessToken = hashText.run(accessToken);

        ResourceOwnerToken resourceOwnerToken = null;
        try {
            resourceOwnerToken = resourceOwnerTokenRepository.getByAccessToken(hashedAccessToken);
        } catch (RecordNotFoundException e) {
            throw new AccessRequestNotFoundException("Could not find resource owner", e);
        }

        RSAPrivateKey key = null;
        try {
            key = rsaPrivateKeyRepository.getMostRecentAndActiveForSigning();
        } catch (RecordNotFoundException e) {
            throw new KeyNotFoundException("No key available to sign id token", e);
        }

        Profile profile = null;
        try {
            profile = profileRepository.getByResourceOwnerId(resourceOwnerToken.getResourceOwner().getUuid());
        } catch (RecordNotFoundException e) {
            throw new ProfileNotFoundException("Profile was not found", e);
        }

        RSAKeyPair rsaKeyPair = privateKeyTranslator.from(key);

        SecureJwtBuilder secureJwtBuilder = null;
        try {
            secureJwtBuilder = jwtAppFactory.secureJwtBuilder(Algorithm.HS256, rsaKeyPair);
        } catch (InvalidAlgorithmException e) {
            throw new IdTokenException("Algorithm to sign with is invalid", e);
        } catch (InvalidJsonWebKeyException e) {
            throw new IdTokenException("key is invalid", e);
        }


        IdToken idToken = idTokenFactory.make(resourceOwnerToken.getToken().getTokenScopes(), profile);

        JsonWebToken jsonWebToken = null;
        try {
            jsonWebToken = secureJwtBuilder.build(idToken);
        } catch (JwtToJsonException e) {
            throw new IdTokenException("Could not build id token", e);
        }

        JWTSerializer jwtSerializer = jwtAppFactory.jwtSerializer();
        String jwt = null;
        try {
            jwt = jwtSerializer.jwtToString(jsonWebToken);
        } catch (JwtToJsonException e) {
            throw new IdTokenException("Could not serialize id token", e);
        }

        return jwt;
    }
}
