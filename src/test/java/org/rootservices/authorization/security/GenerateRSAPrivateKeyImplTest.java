package org.rootservices.authorization.security;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.PrivateKeyUse;
import org.rootservices.authorization.persistence.entity.RSAPrivateKey;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateCrtKey;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 2/15/16.
 */
public class GenerateRSAPrivateKeyImplTest {
    @Mock
    private RSAPrivateKeyFactory mockRSAPrivateKeyFactory;
    private GenerateRSAPrivateKey subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new GenerateRSAPrivateKey(mockRSAPrivateKeyFactory);
    }

    @Test
    public void testGenerateShouldCreateEntity() throws Exception {
        int keySize = 2048;
        PrivateKey mockPrivateKey = mock(PrivateKey.class);

        RSAPrivateCrtKey mockRSAPrivateCrtKey = mock(RSAPrivateCrtKey.class);
        when(mockRSAPrivateCrtKey.getModulus()).thenReturn(new BigInteger("1"));
        when(mockRSAPrivateCrtKey.getPublicExponent()).thenReturn(new BigInteger("2"));
        when(mockRSAPrivateCrtKey.getPrivateExponent()).thenReturn(new BigInteger("3"));
        when(mockRSAPrivateCrtKey.getPrimeP()).thenReturn(new BigInteger("4"));
        when(mockRSAPrivateCrtKey.getPrimeQ()).thenReturn(new BigInteger("5"));
        when(mockRSAPrivateCrtKey.getPrimeExponentP()).thenReturn(new BigInteger("6"));
        when(mockRSAPrivateCrtKey.getPrimeExponentQ()).thenReturn(new BigInteger("7"));
        when(mockRSAPrivateCrtKey.getCrtCoefficient()).thenReturn(new BigInteger("8"));

        when(mockRSAPrivateKeyFactory.makePrivateKey(keySize)).thenReturn(mockPrivateKey);
        when(mockRSAPrivateKeyFactory.makeRSAPrivateCrtKey(mockPrivateKey)).thenReturn(mockRSAPrivateCrtKey);

        RSAPrivateKey actual = subject.generate(keySize);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getUuid(), is(notNullValue()));
        assertThat(actual.getUse(), is(PrivateKeyUse.SIGNATURE));
        assertThat(actual.getModulus(), is(new BigInteger("1")));
        assertThat(actual.getPublicExponent(), is(new BigInteger("2")));
        assertThat(actual.getPrivateExponent(), is(new BigInteger("3")));
        assertThat(actual.getPrimeP(), is(new BigInteger("4")));
        assertThat(actual.getPrimeQ(), is(new BigInteger("5")));
        assertThat(actual.getPrimeExponentP(), is(new BigInteger("6")));
        assertThat(actual.getPrimeExponentQ(), is(new BigInteger("7")));
        assertThat(actual.getCrtCoefficient(), is(new BigInteger("8")));

    }
}