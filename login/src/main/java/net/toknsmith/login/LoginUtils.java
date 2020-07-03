package net.toknsmith.login;

import com.github.benmanes.caffeine.cache.LoadingCache;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.RSAPublicKey;
import net.tokensmith.jwt.entity.jwt.JsonWebToken;
import net.tokensmith.jwt.entity.jwt.header.Algorithm;
import net.tokensmith.jwt.exception.InvalidJWT;
import net.tokensmith.jwt.exception.SignatureException;
import net.tokensmith.jwt.jws.verifier.VerifySignature;
import net.tokensmith.jwt.serialization.JwtSerde;
import net.tokensmith.jwt.serialization.exception.JsonToJwtException;
import net.toknsmith.login.cache.KeyException;
import net.toknsmith.login.endpoint.entity.response.openid.OpenIdToken;
import net.toknsmith.login.endpoint.entity.response.openid.claim.User;
import net.toknsmith.login.exception.CommException;
import net.toknsmith.login.exception.JwtException;
import net.toknsmith.login.exception.TranslateException;
import net.toknsmith.login.model.UserWithTokens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoginUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginUtils.class);
    private LoadingCache<String, RSAPublicKey> keyCache;
    private JwtAppFactory jwtAppFactory;

    public LoginUtils(LoadingCache<String, RSAPublicKey> keyCache, JwtAppFactory jwtAppFactory) {
        this.keyCache = keyCache;
        this.jwtAppFactory = jwtAppFactory;
    }

    /**
     * Given a JWT (id_token) and an OpenIdToken (response from the Id server).
     * - Fetch the corresponding key to verify the JWT's signature
     * - Verify the JWT's signature
     *
     * @param idToken the id token from the id server
     * @param openIdToken the serialized response body from the id server
     * @return A UserWithTokens instance
     * @throws TranslateException when the public key could be translated.
     * @throws CommException when the id server could not be reached
     * @throws JwtException when there is something wrong with the id token (JWT).
     */
    public UserWithTokens toUserWithTokens(JsonWebToken<User> idToken, OpenIdToken openIdToken) throws JwtException {
        Boolean isVerified = verify(idToken);

        if (!isVerified) {
            throw new JwtException("signature is invalid");
        }

        return new UserWithTokens(
                openIdToken.getAccessToken(),
                openIdToken.getRefreshToken(),
                openIdToken.getExpiresIn(),
                openIdToken.getTokenType(),
                idToken.getClaims()
        );
    }

    public User toUser(JsonWebToken<User> idToken) throws JwtException {
        Boolean isVerified = verify(idToken);

        if (!isVerified) {
            throw new JwtException("signature is invalid");
        }

        return idToken.getClaims();
    }

    protected Boolean verify(JsonWebToken<User> idToken) throws JwtException {
        var jwtKeyId = idToken.getHeader().getKeyId();

        Boolean isVerified;
        RSAPublicKey key;

        if (jwtKeyId.isPresent()) {
            try {
                key = keyCache.get(jwtKeyId.get());
            } catch (KeyException e) {
                throw new JwtException("could not get key from id server", e);
            }
        } else {
            LOGGER.debug("no key id on id_token");
            throw new JwtException("no key id on id_token");
        }


        isVerified = isSignatureVerified(idToken, key);
        LOGGER.debug("id_token verified: {}", isVerified);

        if (!isVerified) {
            throw new JwtException("signature is invalid");
        }
        return isVerified;
    }

    public JsonWebToken<User> toJwt(String compactJwt) throws TranslateException {
        JwtSerde jwtSerializer = jwtAppFactory.jwtSerde();
        JsonWebToken<User> jwt;
        try {
            jwt = jwtSerializer.stringToJwt(compactJwt, User.class);
        } catch (JsonToJwtException | InvalidJWT e) {
            throw new TranslateException("Unable to serialize the id token", e);
        }
        return jwt;
    }

    public Boolean isNonceOk(User claims, String expectedNonce) {
        if (claims.getNonce().isPresent()) {
            return expectedNonce.equals(claims.getNonce().get());
        } else {
            return false;
        }
    }

    /**
     * Verifies if a JWT's signature is valid. It must have a sign algorithm and
     * publicKey must be a valid key.
     *
     * @param jwt the JWT to verify
     * @param publicKey the public key to verify the digital signature
     * @return true/false
     * @throws JwtException if there is not sign algorithm on the JWT's header or if the public key is invalid.
     */
    protected Boolean isSignatureVerified(JsonWebToken<User> jwt, RSAPublicKey publicKey) throws JwtException {
        Algorithm sigAlg = jwt.getHeader().getAlgorithm();

        if (sigAlg != Algorithm.RS256 && sigAlg != Algorithm.HS256) {
            String msg = String.format("Invalid sign algorithm for id_token, %s", sigAlg);
            throw new JwtException(msg);
        }

        VerifySignature verifySignature;
        try {
            verifySignature = jwtAppFactory.verifySignature(sigAlg, publicKey);
        } catch (SignatureException e) {
            String msg = String.format("Could not use public key to verify jwt, key id: %s, sign algorithm: %s", publicKey.getKeyId(), sigAlg);
            throw new JwtException(msg, e);
        }

        return verifySignature.run(jwt);
    }
}
