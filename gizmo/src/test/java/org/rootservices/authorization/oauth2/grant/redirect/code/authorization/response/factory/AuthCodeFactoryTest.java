package org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.factory;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.AccessRequest;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.security.ciphers.HashTextStaticSalt;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 4/17/15.
 */
public class AuthCodeFactoryTest {

    @Mock
    private HashTextStaticSalt mockHashText;
    private AuthCodeFactory subject;

    @Before
    public void setUp() throws NoSuchAlgorithmException {
        MockitoAnnotations.initMocks(this);
        subject = new AuthCodeFactory(mockHashText);
    }

    @Test
    public void testRun() throws Exception {
        // parameters to method in test.
        UUID resourceOwnerId = UUID.randomUUID();
        UUID clientUUID = UUID.randomUUID();
        Long secondsToExpire = 600L;
        String randomString = "randomString";
        String hashedRandomString = "hashedRandomString";

        when(mockHashText.run(randomString)).thenReturn(hashedRandomString);

        AccessRequest ar = FixtureFactory.makeAccessRequest(resourceOwnerId, clientUUID);
        AuthCode actual = subject.makeAuthCode(ar, randomString, secondsToExpire);

        assertThat(actual.getId(), is(notNullValue()));
        assertThat(actual.getCode(), is(hashedRandomString.getBytes()));
        assertThat(actual.getAccessRequest(), is(ar));
    }
}