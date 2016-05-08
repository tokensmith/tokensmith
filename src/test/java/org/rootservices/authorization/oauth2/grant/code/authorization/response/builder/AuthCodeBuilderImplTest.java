package org.rootservices.authorization.oauth2.grant.code.authorization.response.builder;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.oauth2.grant.code.authorization.response.builder.AuthCodeBuilder;
import org.rootservices.authorization.oauth2.grant.code.authorization.response.builder.AuthCodeBuilderImpl;
import org.rootservices.authorization.persistence.entity.AccessRequest;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.security.HashTextStaticSalt;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 4/17/15.
 */
public class AuthCodeBuilderImplTest {

    @Mock
    private HashTextStaticSalt mockHashText;
    private AuthCodeBuilder subject;

    @Before
    public void setUp() throws NoSuchAlgorithmException {
        MockitoAnnotations.initMocks(this);
        subject = new AuthCodeBuilderImpl(mockHashText);
    }

    @Test
    public void testRun() throws Exception {
        // parameters to method in test.
        UUID resourceOwnerId = UUID.randomUUID();
        UUID clientUUID = UUID.randomUUID();
        int secondsToExpire = 60*10;
        String randomString = "randomString";
        String hashedRandomString = "hashedRandomString";

        when(mockHashText.run(randomString)).thenReturn(hashedRandomString);

        AccessRequest ar = FixtureFactory.makeAccessRequest(resourceOwnerId, clientUUID);
        AuthCode actual = subject.run(ar, randomString, secondsToExpire);

        assertThat(actual.getUuid()).isNotNull();
        assertThat(actual.getCode()).isEqualTo(hashedRandomString.getBytes());
        assertThat(actual.getAccessRequest()).isEqualTo(ar);
    }
}