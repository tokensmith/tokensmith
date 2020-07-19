package net.toknsmith.login.translator;


import helper.Factory;
import net.tokensmith.jwt.entity.jwk.KeyType;
import net.tokensmith.jwt.entity.jwk.RSAPublicKey;
import net.toknsmith.login.config.LoginFactory;
import net.toknsmith.login.exception.TranslateException;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class JwtRSAPublicKeyTranslatorTest {
    private JwtRSAPublicKeyTranslator subject;
    private LoginFactory loginFactory;

    @Before
    public void setUp() throws Exception {
        loginFactory = new LoginFactory();
        subject = new JwtRSAPublicKeyTranslator(loginFactory.objectMapper());
    }

    @Test
    public void toSingle() throws Exception {
        net.toknsmith.login.endpoint.entity.response.api.key.RSAPublicKey serverKey = Factory.serverKey();
        String expectedBody = loginFactory.objectMapper().writeValueAsString(serverKey);

        InputStream body = new ByteArrayInputStream(expectedBody.getBytes(StandardCharsets.UTF_8));

        RSAPublicKey actual = subject.toSingle(body);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getKeyId().get(), is(serverKey.getKeyId().toString()));
        assertThat(actual.getKeyType(), is(KeyType.RSA));
        assertThat(actual.getE(), is(serverKey.getE()));
        assertThat(actual.getN(), is(serverKey.getN()));
    }

    @Test
    public void toSingleShouldThrowTranslationException() throws Exception {

        String expectedBody = "unexpected-body";

        InputStream body = new ByteArrayInputStream(expectedBody.getBytes(StandardCharsets.UTF_8));

        TranslateException actual = null;
        try {
            subject.toSingle(body);
        } catch (TranslateException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
    }
}