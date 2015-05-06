package org.rootservices.authorization.grant.code.authenticate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.security.TextHasher;

import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 4/17/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class MakeAuthCodeImplTest {

    @Mock
    private TextHasher mockTextHasher;

    private MakeAuthCode subject;

    @Before
    public void setUp() {
        subject = new MakeAuthCodeImpl(mockTextHasher);
    }

    @Test
    public void testRun() throws Exception {
        // parameters to method in test.
        UUID resourceOwnerUUID = UUID.randomUUID();
        UUID clientUUID = UUID.randomUUID();
        int secondsToExpire = 60*10;
        String randomString = "randomString";

        String hashedRandomString = "hashedRandomString";
        when(mockTextHasher.run(randomString)).thenReturn(hashedRandomString);

        AuthCode actual = subject.run(resourceOwnerUUID, clientUUID, randomString, secondsToExpire);

        assertThat(actual.getUuid()).isNotNull();
        assertThat(actual.getResourceOwnerUUID()).isEqualTo(resourceOwnerUUID);
        assertThat(actual.getClientUUID()).isEqualTo(clientUUID);
        assertThat(actual.getCode()).isEqualTo(hashedRandomString.getBytes());
    }
}