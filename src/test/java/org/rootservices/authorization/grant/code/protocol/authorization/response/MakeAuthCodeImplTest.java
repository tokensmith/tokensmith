package org.rootservices.authorization.grant.code.protocol.authorization.response;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
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
@RunWith(MockitoJUnitRunner.class)
public class MakeAuthCodeImplTest {

    @Mock
    private HashTextStaticSalt mockHashText;
    private MakeAuthCode subject;

    @Before
    public void setUp() throws NoSuchAlgorithmException {
        subject = new MakeAuthCodeImpl(mockHashText);
    }

    @Test
    public void testRun() throws Exception {
        // parameters to method in test.
        UUID resourceOwnerUUID = UUID.randomUUID();
        UUID clientUUID = UUID.randomUUID();
        int secondsToExpire = 60*10;
        String randomString = "randomString";
        String hashedRandomString = "hashedRandomString";

        when(mockHashText.run(randomString)).thenReturn(hashedRandomString);

        AccessRequest ar = FixtureFactory.makeAccessRequest(resourceOwnerUUID, clientUUID);
        AuthCode actual = subject.run(ar, randomString, secondsToExpire);

        assertThat(actual.getUuid()).isNotNull();
        assertThat(actual.getCode()).isEqualTo(hashedRandomString.getBytes());
        assertThat(actual.getAccessRequest()).isEqualTo(ar);
    }
}