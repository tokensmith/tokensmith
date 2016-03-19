package org.rootservices.authorization.grant.openid.protocol.token;

import org.rootservices.authorization.grant.openid.protocol.token.exception.IdTokenException;
import org.rootservices.authorization.grant.openid.protocol.token.exception.KeyNotFoundException;
import org.rootservices.authorization.grant.openid.protocol.token.exception.ResourceOwnerNotFoundException;
import org.rootservices.authorization.grant.openid.protocol.token.response.entity.IdToken;
import org.rootservices.authorization.grant.openid.protocol.token.translator.PrivateKeyTranslator;
import org.rootservices.authorization.persistence.entity.RSAPrivateKey;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ResourceOwnerRepository;
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

import java.util.List;
import java.util.Optional;

/**
 * Created by tommackenzie on 1/24/16.
 *
 * // add indexes.
 */
@Component
public class BuildIdentityTokenImpl implements BuildIdentityToken {
    private HashTextStaticSalt hashText;
    private RsaPrivateKeyRepository rsaPrivateKeyRepository;
    private ResourceOwnerRepository resourceOwnerRepository;
    private PrivateKeyTranslator privateKeyTranslator;
    private AppFactory jwtAppFactory;

    @Autowired
    public BuildIdentityTokenImpl(HashTextStaticSalt hashText, RsaPrivateKeyRepository rsaPrivateKeyRepository, ResourceOwnerRepository resourceOwnerRepository, PrivateKeyTranslator privateKeyTranslator, AppFactory jwtAppFactory) {
        this.hashText = hashText;
        this.rsaPrivateKeyRepository = rsaPrivateKeyRepository;
        this.resourceOwnerRepository = resourceOwnerRepository;
        this.privateKeyTranslator = privateKeyTranslator;
        this.jwtAppFactory = jwtAppFactory;
    }

    @Override
    public String build(String accessToken) throws ResourceOwnerNotFoundException, IdTokenException, KeyNotFoundException {

        String hashedAccessToken = hashText.run(accessToken);

        ResourceOwner resourceOwner = null;
        try {
            resourceOwner = resourceOwnerRepository.getByAccessToken(hashedAccessToken.getBytes());
        } catch (RecordNotFoundException e) {
            throw new ResourceOwnerNotFoundException("Could not find resource owner", e);
        }

        RSAPrivateKey key = null;
        try {
            key = rsaPrivateKeyRepository.getMostRecentAndActiveForSigning();
        } catch (RecordNotFoundException e) {
            throw new KeyNotFoundException("No key available to sign id token", e);
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

        // TODO: need to write a factory for idToken
        IdToken idToken = new IdToken();
        idToken.setEmail(Optional.of(resourceOwner.getEmail()));

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
