package net.toknsmith.login.cache;

import helper.Factory;
import net.tokensmith.jwt.entity.jwk.RSAPublicKey;
import net.toknsmith.login.endpoint.KeyEndpoint;
import net.toknsmith.login.endpoint.entity.response.api.ClientError;
import net.toknsmith.login.endpoint.entity.response.api.ServerError;
import net.toknsmith.login.exception.CommException;
import net.toknsmith.login.exception.TranslateException;
import net.toknsmith.login.exception.URLException;
import net.toknsmith.login.exception.http.api.ClientException;
import net.toknsmith.login.exception.http.api.ServerException;
import net.toknsmith.login.http.StatusCode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class KeyCacheLoaderTest {

    private KeyCacheLoader subject;

    @Mock
    private KeyEndpoint mockKeyEndpoint;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new KeyCacheLoader(mockKeyEndpoint);
    }

    @Test
    public void loadShouldReturnKey() throws Exception {
        RSAPublicKey key = Factory.rsaPublicKeyTranslated();

        when(mockKeyEndpoint.getKey(key.getKeyId().get())).thenReturn(key);

        RSAPublicKey actual = subject.load(key.getKeyId().get());

        assertThat(actual, is(key));
    }

    @Test
    public void loadWhenURLExceptionShouldThrowKeyException() throws Exception {
        RSAPublicKey key = Factory.rsaPublicKeyTranslated();

        URLException expected = new URLException("", new RuntimeException());
        doThrow(expected).when(mockKeyEndpoint).getKey(key.getKeyId().get());

        KeyException actual = null;
        try {
            subject.load(key.getKeyId().get());
        } catch (KeyException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(expected));
    }


    @Test
    public void loadWhenCommExceptionShouldThrowKeyException() throws Exception {
        RSAPublicKey key = Factory.rsaPublicKeyTranslated();

        CommException expected = new CommException("", new RuntimeException());
        doThrow(expected).when(mockKeyEndpoint).getKey(key.getKeyId().get());

        KeyException actual = null;
        try {
            subject.load(key.getKeyId().get());
        } catch (KeyException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(expected));
    }

    @Test
    public void loadWhenTranslateExceptionShouldThrowKeyException() throws Exception {
        RSAPublicKey key = Factory.rsaPublicKeyTranslated();

        TranslateException expected = new TranslateException("", new RuntimeException());
        doThrow(expected).when(mockKeyEndpoint).getKey(key.getKeyId().get());

        KeyException actual = null;
        try {
            subject.load(key.getKeyId().get());
        } catch (KeyException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(expected));
    }

    @Test
    public void loadWhenClientExceptionShouldThrowKeyException() throws Exception {
        RSAPublicKey key = Factory.rsaPublicKeyTranslated();

        ClientException expected = new ClientException("", StatusCode.NOT_FOUND.getCode(), new ClientError());
        doThrow(expected).when(mockKeyEndpoint).getKey(key.getKeyId().get());

        KeyException actual = null;
        try {
            subject.load(key.getKeyId().get());
        } catch (KeyException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(expected));
    }

    @Test
    public void loadWhenServerExceptionShouldThrowKeyException() throws Exception {
        RSAPublicKey key = Factory.rsaPublicKeyTranslated();

        ServerException expected = new ServerException("", StatusCode.SERVER_ERROR.getCode(), new ServerError());
        doThrow(expected).when(mockKeyEndpoint).getKey(key.getKeyId().get());

        KeyException actual = null;
        try {
            subject.load(key.getKeyId().get());
        } catch (KeyException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(expected));
    }
}