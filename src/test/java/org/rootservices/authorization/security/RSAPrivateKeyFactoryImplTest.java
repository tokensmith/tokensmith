package org.rootservices.authorization.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateCrtKey;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 2/14/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
public class RSAPrivateKeyFactoryImplTest {

    @Autowired
    private RSAPrivateKeyFactory subject;

    @Test
    public void testMakePrivateKey() throws Exception {
        PrivateKey actual = subject.makePrivateKey(1024);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAlgorithm(), is("RSA"));
    }

    @Test
    public void testMakeRSAPrivateCrtKey() throws Exception {
        PrivateKey privateKey = subject.makePrivateKey(1024);
        RSAPrivateCrtKey actual = subject.makeRSAPrivateCrtKey(privateKey);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAlgorithm(), is("RSA"));
        assertThat(actual.getModulus().bitLength(), is(1024));
    }
}