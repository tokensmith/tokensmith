package org.rootservices.authorization.grant.code.protocol.authorization.response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.grant.code.protocol.authorization.exception.AuthCodeInsertException;
import org.rootservices.authorization.persistence.entity.AccessRequest;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.AuthCodeRepository;
import org.rootservices.authorization.security.RandomString;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by tommackenzie on 7/16/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class InsertAuthCodeWithRetryImplTest {

    private InsertAuthCodeWithRetry subject;
    @Mock
    private RandomString mockRandomString;
    @Mock
    private AuthCodeBuilder mockAuthCodeBuilder;
    @Mock
    private AuthCodeRepository mockAuthCodeRepository;

    @Before
    public void setUp() {
        subject = new InsertAuthCodeWithRetryImpl(mockRandomString, mockAuthCodeBuilder, mockAuthCodeRepository);
    }

    @Test
    public void testRun() throws Exception {
        AccessRequest accessRequest = new AccessRequest();

        String authorizationCode = "randomString";
        when(mockRandomString.run()).thenReturn(authorizationCode);
        AuthCode authCode = new AuthCode();
        when(mockAuthCodeBuilder.run(accessRequest, authorizationCode, subject.getSecondsToExpiration())).thenReturn(authCode);

        String actual = subject.run(accessRequest, 1);
        assertThat(actual).isEqualTo(authorizationCode);
        verify(mockAuthCodeRepository).insert(authCode);
    }

    @Test
    public void testDuplicateFound() throws Exception {
        AccessRequest accessRequest = new AccessRequest();

        String authorizationCode = "randomString";
        when(mockRandomString.run()).thenReturn(authorizationCode);
        AuthCode authCode = new AuthCode();
        when(mockAuthCodeBuilder.run(accessRequest, authorizationCode, subject.getSecondsToExpiration())).thenReturn(authCode);

        doThrow(DuplicateRecordException.class)
        .doNothing().when(mockAuthCodeRepository).insert(authCode);

        String actual = subject.run(accessRequest, 1);
        assertThat(actual).isEqualTo(authorizationCode);

        verify(mockAuthCodeRepository, times(2)).insert(authCode);
    }

    @Test(expected = AuthCodeInsertException.class)
    public void testReachesMaxRetries() throws Exception {
        AccessRequest accessRequest = new AccessRequest();

        String authorizationCode = "randomString";
        when(mockRandomString.run()).thenReturn(authorizationCode);
        AuthCode authCode = new AuthCode();
        when(mockAuthCodeBuilder.run(accessRequest, authorizationCode, subject.getSecondsToExpiration())).thenReturn(authCode);

        doThrow(DuplicateRecordException.class)
        .doThrow(DuplicateRecordException.class)
        .doThrow(DuplicateRecordException.class).when(mockAuthCodeRepository).insert(authCode);

        subject.run(accessRequest, 1);
    }
}