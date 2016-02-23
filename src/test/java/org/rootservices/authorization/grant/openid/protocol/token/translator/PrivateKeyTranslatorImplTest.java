package org.rootservices.authorization.grant.openid.protocol.token.translator;

import org.junit.Before;
import org.junit.Test;
import org.rootservices.authorization.persistence.entity.RSAPrivateKey;
import org.rootservices.authorization.persistence.entity.PrivateKeyUse;
import org.rootservices.jwt.entity.jwk.KeyType;
import org.rootservices.jwt.entity.jwk.RSAKeyPair;
import org.rootservices.jwt.entity.jwk.Use;

import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 2/12/16.
 */
public class PrivateKeyTranslatorImplTest {

    private PrivateKeyTranslator subject;

    @Before
    public void setUp() {
        subject = new PrivateKeyTranslatorImpl();
    }

    @Test
    public void toShouldTranslate() throws Exception {
        RSAKeyPair keyPair = new RSAKeyPair(
                Optional.empty(),
                KeyType.RSA,
                Use.SIGNATURE,
                new BigInteger("1"),
                new BigInteger("2"),
                new BigInteger("3"),
                new BigInteger("4"),
                new BigInteger("5"),
                new BigInteger("6"),
                new BigInteger("7"),
                new BigInteger("8")
        );

        RSAPrivateKey actual = subject.to(keyPair);

        assertThat(actual.getUuid(), is(nullValue()));
        assertThat(actual.getUse(), is(PrivateKeyUse.SIGNATURE));
        assertThat(actual.getModulus(), is(keyPair.getN()));
        assertThat(actual.getPublicExponent(), is(keyPair.getE()));
        assertThat(actual.getPrivateExponent(), is(keyPair.getD()));
        assertThat(actual.getPrimeP(), is(keyPair.getP()));
        assertThat(actual.getPrimeQ(), is(keyPair.getQ()));
        assertThat(actual.getPrimeExponentP(), is(keyPair.getDp()));
        assertThat(actual.getPrimeExponentQ(), is(keyPair.getDq()));
        assertThat(actual.getCrtCoefficient(), is(keyPair.getQi()));
    }

    @Test
    public void fromShouldTranslate() throws Exception {

        RSAPrivateKey privateKey = new RSAPrivateKey();
        privateKey.setUuid(UUID.randomUUID());
        privateKey.setUse(PrivateKeyUse.SIGNATURE);
        privateKey.setModulus(new BigInteger("1"));
        privateKey.setPublicExponent(new BigInteger("2"));
        privateKey.setPrivateExponent(new BigInteger("3"));
        privateKey.setPrimeP(new BigInteger("4"));
        privateKey.setPrimeQ(new BigInteger("5"));
        privateKey.setPrimeExponentP(new BigInteger("6"));
        privateKey.setPrimeExponentQ(new BigInteger("7"));
        privateKey.setCrtCoefficient(new BigInteger("8"));

        RSAKeyPair actual = subject.from(privateKey);

        assertThat(actual.getUse(), is(Use.SIGNATURE));
        assertThat(actual.getN(), is(privateKey.getModulus()));
        assertThat(actual.getE(), is(privateKey.getPublicExponent()));
        assertThat(actual.getD(), is(privateKey.getPrivateExponent()));
        assertThat(actual.getP(), is(privateKey.getPrimeP()));
        assertThat(actual.getQ(), is(privateKey.getPrimeQ()));
        assertThat(actual.getDp(), is(privateKey.getPrimeExponentP()));
        assertThat(actual.getDq(), is(privateKey.getPrimeExponentQ()));
        assertThat(actual.getQi(), is(privateKey.getCrtCoefficient()));
    }
}