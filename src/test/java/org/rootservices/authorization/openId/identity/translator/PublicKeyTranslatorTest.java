package org.rootservices.authorization.openId.identity.translator;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.authorization.openId.identity.entity.RSAPublicKey;
import org.rootservices.authorization.persistence.entity.RSAPrivateKey;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 1/1/17.
 */
public class PublicKeyTranslatorTest {
    private PublicKeyTranslator subject;

    @Before
    public void setUp() {
        subject = new PublicKeyTranslator();
    }

    @Test
    public void toShouldReturnPublicKey() throws Exception {
        RSAPrivateKey rsaPrivateKey = FixtureFactory.makeRSAPrivateKey();

        RSAPublicKey actual = subject.to(rsaPrivateKey);

        assertThat(actual.getId(), is(rsaPrivateKey.getId()));
        assertThat(actual.getModulus(), is(rsaPrivateKey.getModulus()));
        assertThat(actual.getPublicExponent(), is(rsaPrivateKey.getPublicExponent()));
    }

}