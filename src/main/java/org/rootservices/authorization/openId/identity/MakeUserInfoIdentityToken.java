package org.rootservices.authorization.openId.identity;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenClaims;
import org.rootservices.authorization.openId.identity.entity.IdToken;
import org.rootservices.authorization.openId.identity.exception.IdTokenException;
import org.rootservices.authorization.openId.identity.exception.KeyNotFoundException;
import org.rootservices.authorization.openId.identity.exception.ProfileNotFoundException;
import org.rootservices.authorization.openId.identity.exception.ResourceOwnerNotFoundException;
import org.rootservices.authorization.openId.identity.factory.IdTokenFactory;
import org.rootservices.authorization.openId.identity.translator.PrivateKeyTranslator;
import org.rootservices.authorization.persistence.entity.RSAPrivateKey;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ResourceOwnerRepository;
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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by tommackenzie on 1/24/16.
 *
 * // add indexes.
 */
@Component
public class MakeUserInfoIdentityToken {
    private static final Logger logger = LogManager.getLogger(MakeUserInfoIdentityToken.class);

    private HashTextStaticSalt hashText;
    private ResourceOwnerRepository resourceOwnerRepository;
    private RsaPrivateKeyRepository rsaPrivateKeyRepository;
    private PrivateKeyTranslator privateKeyTranslator;
    private AppFactory jwtAppFactory;
    private IdTokenFactory idTokenFactory;
    private String issuer;

    private static String RESOURCE_OWNER_NOT_FOUND = "resource owner was not found";
    private static String KEY_NOT_FOUND = "No key available to sign id token";
    private static String ALG_INVALID = "Algorithm to sign with is invalid";
    private static String KEY_INVALID = "key is invalid";
    private static String SERIALIZE_ERROR = "Could not serialize id token";

    @Autowired
    public MakeUserInfoIdentityToken(HashTextStaticSalt hashText, ResourceOwnerRepository resourceOwnerRepository, RsaPrivateKeyRepository rsaPrivateKeyRepository, PrivateKeyTranslator privateKeyTranslator, AppFactory jwtAppFactory, IdTokenFactory idTokenFactory, String issuer) {
        this.hashText = hashText;
        this.resourceOwnerRepository = resourceOwnerRepository;
        this.rsaPrivateKeyRepository = rsaPrivateKeyRepository;
        this.privateKeyTranslator = privateKeyTranslator;
        this.jwtAppFactory = jwtAppFactory;
        this.idTokenFactory = idTokenFactory;
        this.issuer = issuer;
    }

    public String make(String accessToken) throws IdTokenException, KeyNotFoundException, ProfileNotFoundException, ResourceOwnerNotFoundException {
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

        TokenClaims tc = makeTokenClaims(ro.getTokens().get(0));

        // TODO: NPE - for ro.getTokens()
        List<String> scopesForIdToken = ro.getTokens().get(0).getTokenScopes().stream()
                .map(item -> item.getScope().getName())
                .collect(Collectors.toList());

        /*
         TODO: remove Token Claims and rely on token.
         https://www.pivotaltracker.com/story/show/136181907
          */
        IdToken idToken = idTokenFactory.make(tc, scopesForIdToken, ro);

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
