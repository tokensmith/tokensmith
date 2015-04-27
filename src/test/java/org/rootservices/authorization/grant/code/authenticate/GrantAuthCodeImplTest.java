package org.rootservices.authorization.grant.code.authenticate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.grant.code.request.AuthRequest;
import org.rootservices.authorization.persistence.entity.AccessRequest;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.entity.Scope;
import org.rootservices.authorization.persistence.repository.AccessRequestRepository;
import org.rootservices.authorization.persistence.repository.AuthCodeRepository;
import org.rootservices.authorization.security.RandomString;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 4/23/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class GrantAuthCodeImplTest {
    @Mock
    private RandomString mockRandomString;
    @Mock
    private MakeAuthCode mockMakeAuthCode;
    @Mock
    private AuthCodeRepository mockAuthCodeRepository;
    @Mock
    private AccessRequestRepository mockAccessRequestRepository;

    private GrantAuthCode subject;

    @Before
    public void setUp() {
        subject = new GrantAuthCodeImpl(
                mockRandomString, mockMakeAuthCode, mockAuthCodeRepository, mockAccessRequestRepository
        );
    }

    @Test
    public void testRun() throws Exception {
        // parameters to pass into method in test
        UUID resourceOwnerUUID = UUID.randomUUID();
        UUID clientUUID = UUID.randomUUID();
        Optional<URI> redirectURI = Optional.of(new URI("https://rootservices.org"));

        // responses from mocks/spy objects.
        String randomString = "randomString";
        AuthCode authCode = new AuthCode();
        authCode.setUuid(UUID.randomUUID());

        when(mockRandomString.run()).thenReturn(randomString);
        when(mockMakeAuthCode.run(
                resourceOwnerUUID, clientUUID, randomString, subject.getSecondsToExpiration()
        )).thenReturn(authCode);

        String actual = subject.run(resourceOwnerUUID, clientUUID, redirectURI);

        assertThat(actual).isEqualTo(randomString);
        verify(mockAuthCodeRepository).insert(authCode);
        verify(mockAccessRequestRepository).insert(any(AccessRequest.class));

    }
}