package org.rootservices.authorization.openId.identity;

import org.rootservices.authorization.openId.identity.entity.IdToken;
import org.rootservices.authorization.openId.identity.exception.IdTokenException;
import org.rootservices.authorization.openId.identity.exception.KeyNotFoundException;
import org.rootservices.authorization.openId.identity.exception.ProfileNotFoundException;
import org.rootservices.authorization.openId.identity.factory.IdTokenFactory;
import org.rootservices.authorization.openId.identity.translator.PrivateKeyTranslator;
import org.rootservices.authorization.persistence.entity.Profile;
import org.rootservices.authorization.persistence.entity.RSAPrivateKey;
import org.rootservices.authorization.persistence.entity.Scope;
import org.rootservices.authorization.persistence.entity.TokenScope;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ProfileRepository;
import org.rootservices.authorization.persistence.repository.RsaPrivateKeyRepository;
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
import java.util.UUID;

/**
 * Created by tommackenzie on 8/31/16.
 */
@Component
public class MakeImplicitIdentityToken {
    private ProfileRepository profileRepository;
    private MakeAccessTokenHash makeAccessTokenHash;
    private IdTokenFactory idTokenFactory;
    private RsaPrivateKeyRepository rsaPrivateKeyRepository;
    private PrivateKeyTranslator privateKeyTranslator;
    private AppFactory jwtAppFactory;

    @Autowired
    public MakeImplicitIdentityToken(ProfileRepository profileRepository, MakeAccessTokenHash makeAccessTokenHash, IdTokenFactory idTokenFactory, RsaPrivateKeyRepository rsaPrivateKeyRepository, PrivateKeyTranslator privateKeyTranslator, AppFactory jwtAppFactory) {
        this.profileRepository = profileRepository;
        this.makeAccessTokenHash = makeAccessTokenHash;
        this.idTokenFactory = idTokenFactory;
        this.rsaPrivateKeyRepository = rsaPrivateKeyRepository;
        this.privateKeyTranslator = privateKeyTranslator;
        this.jwtAppFactory = jwtAppFactory;
    }

    /**
     * Creates a id token for the implicit grant flow, "token id_token".
     * http://openid.net/specs/openid-connect-core-1_0.html#ImplicitFlowAuth
     *
     * @param plainTextAccessToken
     * @param nonce
     * @param resourceOwnerId
     * @param scopesForIdToken
     * @return a secure (signed) and encoded jwt
     * @throws ProfileNotFoundException
     * @throws KeyNotFoundException
     * @throws IdTokenException
     */
    public String makeForAccessToken(String plainTextAccessToken, String nonce, UUID resourceOwnerId, List<String> scopesForIdToken) throws ProfileNotFoundException, KeyNotFoundException, IdTokenException {

        Profile profile = null;
        try {
            profile = profileRepository.getByResourceOwnerId(resourceOwnerId);
        } catch (RecordNotFoundException e) {
            throw new ProfileNotFoundException("Profile was not found", e);
        }

        String accessTokenHash = makeAccessTokenHash.makeEncodedHash(plainTextAccessToken);
        IdToken idToken = idTokenFactory.make(accessTokenHash, nonce, scopesForIdToken, profile);

        RSAPrivateKey key = null;
        try {
            key = rsaPrivateKeyRepository.getMostRecentAndActiveForSigning();
        } catch (RecordNotFoundException e) {
            throw new KeyNotFoundException("No key available to sign id token", e);
        }

        RSAKeyPair rsaKeyPair = privateKeyTranslator.from(key);
        String encodedJwt = translateIdTokenToEncodedJwt(rsaKeyPair, idToken);

        return encodedJwt;
    }

    public String makeIdentityOnly(String nonce, UUID resourceOwnerId, List<Scope> scopes) throws ProfileNotFoundException, KeyNotFoundException, IdTokenException {

        Profile profile = null;
        try {
            profile = profileRepository.getByResourceOwnerId(resourceOwnerId);
        } catch (RecordNotFoundException e) {
            throw new ProfileNotFoundException("Profile was not found", e);
        }

        IdToken idToken = null;

        RSAPrivateKey key = null;
        try {
            key = rsaPrivateKeyRepository.getMostRecentAndActiveForSigning();
        } catch (RecordNotFoundException e) {
            throw new KeyNotFoundException("No key available to sign id token", e);
        }

        RSAKeyPair rsaKeyPair = privateKeyTranslator.from(key);
        String encodedJwt = translateIdTokenToEncodedJwt(rsaKeyPair, idToken);

        return encodedJwt;
    }

    protected String translateIdTokenToEncodedJwt(RSAKeyPair rsaKeyPair, IdToken idToken) throws IdTokenException {

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
