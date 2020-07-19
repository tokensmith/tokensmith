package net.toknsmith.login;

import com.github.benmanes.caffeine.cache.LoadingCache;
import helper.Factory;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.RSAPublicKey;
import net.tokensmith.jwt.entity.jwt.JsonWebToken;
import net.toknsmith.login.cache.KeyException;
import net.toknsmith.login.endpoint.entity.response.openid.OpenIdToken;
import net.toknsmith.login.endpoint.entity.response.openid.claim.User;
import net.toknsmith.login.exception.JwtException;
import net.toknsmith.login.exception.TranslateException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class LoginUtilsTest {

    private LoginUtils subject;

    @Mock
    private LoadingCache<String, RSAPublicKey> mockKeyCache;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new LoginUtils(mockKeyCache, new JwtAppFactory());
    }

    @Test
    public void toJwtShouldBeOk() throws Exception {
        String compactJwt = Factory.okIdToken().getIdToken();

        JsonWebToken<User> actual = subject.toJwt(compactJwt);

        assertThat(actual, is(notNullValue()));

        User actualUser = actual.getClaims();
        assertThat(actualUser.getEmail(), is(notNullValue()));
    }

    @Test
    public void toJwtWhenInvalidShouldThrowTranslateException() throws Exception {
        String compactJwt = "invalid jwt";

        TranslateException actual = null;
        try {
            subject.toJwt(compactJwt);
        } catch (TranslateException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void toUserWithTokensWhenKeyExceptionShouldThrowJwtException() throws Exception {
        String keyId = UUID.randomUUID().toString();
        JsonWebToken<User> idToken = Factory.idToken(keyId);
        OpenIdToken payload = Factory.okIdToken();

        KeyException expected = new KeyException("", new RuntimeException());
        doThrow(expected).when(mockKeyCache).get(keyId);

        JwtException actual = null;
        try {
            subject.toUserWithTokens(idToken, payload);
        } catch (JwtException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(expected));
    }

    @Test
    public void toUserWithTokensShouldThrowJwtException() throws Exception {
        String keyId = UUID.randomUUID().toString();
        JsonWebToken<User> idToken = Factory.idToken(keyId);
        idToken.getHeader().setKeyId(Optional.empty());
        OpenIdToken payload = Factory.okIdToken();


        JwtException actual = null;
        try {
            subject.toUserWithTokens(idToken, payload);
        } catch (JwtException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void toUserWithTokensWhenNotVerifiedShouldThrowJwtException() throws Exception {
        OpenIdToken openIdToken = Factory.idTokenWithBadSignature();
        JsonWebToken<User> idToken = subject.toJwt(openIdToken.getIdToken());

        RSAPublicKey key = Factory.rsaPublicKeyTranslated();
        when(
            mockKeyCache.get(idToken.getHeader().getKeyId().get())
        ).thenReturn(key);

        JwtException actual = null;
        try {
            subject.toUserWithTokens(idToken, openIdToken);
        } catch (JwtException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void toUserWhenKeyExceptionShouldThrowJwtException() throws Exception {
        String keyId = UUID.randomUUID().toString();
        JsonWebToken<User> idToken = Factory.idToken(keyId);

        KeyException expected = new KeyException("", new RuntimeException());
        doThrow(expected).when(mockKeyCache).get(keyId);

        JwtException actual = null;
        try {
            subject.toUser(idToken);
        } catch (JwtException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(expected));
    }

    @Test
    public void toUserWhenKeyIdEmptyShouldThrowJwtException() throws Exception {
        String keyId = UUID.randomUUID().toString();
        JsonWebToken<User> idToken = Factory.idToken(keyId);
        idToken.getHeader().setKeyId(Optional.empty());

        JwtException actual = null;
        try {
            subject.toUser(idToken);
        } catch (JwtException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void toUserWhenNotVerifiedShouldThrowJwtException() throws Exception {
        OpenIdToken openIdToken = Factory.idTokenWithBadSignature();
        JsonWebToken<User> idToken = subject.toJwt(openIdToken.getIdToken());

        RSAPublicKey key = Factory.rsaPublicKeyTranslated();
        when(
                mockKeyCache.get(idToken.getHeader().getKeyId().get())
        ).thenReturn(key);

        JwtException actual = null;
        try {
            subject.toUser(idToken);
        } catch (JwtException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void isNonceOkShouldReturnTrue() {
        User user = new User();
        user.setNonce(Optional.of("nonce-123"));

        Boolean actual = subject.isNonceOk(user, "nonce-123");

        assertTrue(actual);
    }

    @Test
    public void isNonceOkWhenDoesNotMatchShouldReturnFalse() {
        User user = new User();
        user.setNonce(Optional.of("nonce-123"));

        Boolean actual = subject.isNonceOk(user, "nonce-1234");

        assertFalse(actual);
    }

    @Test
    public void isNonceOkWhenNotPresentShouldReturnFalse() {
        User user = new User();
        user.setNonce(Optional.empty());

        Boolean actual = subject.isNonceOk(user, "nonce-123");

        assertFalse(actual);
    }
}