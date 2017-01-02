package org.rootservices.authorization.openId.jwk.translator;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;

import org.rootservices.authorization.persistence.entity.RSAPrivateKey;
import org.rootservices.jwt.entity.jwk.KeyType;
import org.rootservices.jwt.entity.jwk.RSAPublicKey;
import org.rootservices.jwt.entity.jwk.Use;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 1/1/17.
 */
public class RSAPublicKeyTranslatorTest {
    private RSAPublicKeyTranslator subject;

    @Before
    public void setUp() {
        subject = new RSAPublicKeyTranslator();
    }

    @Test
    public void toShouldReturnPublicKey() throws Exception {
        RSAPrivateKey rsaPrivateKey = FixtureFactory.makeRSAPrivateKey();

        RSAPublicKey actual = subject.to(rsaPrivateKey);

        assertThat(actual.getKeyId().isPresent(), is(true));
        assertThat(actual.getKeyId().get(), is(rsaPrivateKey.getId().toString()));
        assertThat(actual.getKeyType(), is(KeyType.RSA));
        assertThat(actual.getUse(), is(Use.SIGNATURE));
        assertThat(actual.getN(), is(rsaPrivateKey.getModulus()));
        assertThat(actual.getE(), is(rsaPrivateKey.getPublicExponent()));
    }

}