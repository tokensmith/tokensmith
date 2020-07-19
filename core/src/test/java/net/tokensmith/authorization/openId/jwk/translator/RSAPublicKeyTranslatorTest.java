package net.tokensmith.authorization.openId.jwk.translator;

import helper.fixture.FixtureFactory;
import net.tokensmith.authorization.openId.jwk.entity.RSAPublicKey;
import net.tokensmith.repository.entity.RSAPrivateKey;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

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

        assertThat(actual.getKeyId(), is(rsaPrivateKey.getId()));
        assertThat(actual.getUse(), is(rsaPrivateKey.getUse()));
        assertThat(actual.getN(), is(rsaPrivateKey.getModulus()));
        assertThat(actual.getE(), is(rsaPrivateKey.getPublicExponent()));
    }

}