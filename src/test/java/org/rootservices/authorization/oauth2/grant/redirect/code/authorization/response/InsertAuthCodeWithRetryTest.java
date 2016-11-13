package org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.factory.AuthCodeFactory;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.exception.AuthCodeInsertException;
import org.rootservices.authorization.persistence.entity.AccessRequest;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.AuthCodeRepository;
import org.rootservices.authorization.security.RandomString;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by tommackenzie on 7/16/15.
 */
public class InsertAuthCodeWithRetryTest {

    private InsertAuthCodeWithRetry subject;
    @Mock
    private RandomString mockRandomString;
    @Mock
    private AuthCodeFactory mockAuthCodeFactory;
    @Mock
    private AuthCodeRepository mockAuthCodeRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new InsertAuthCodeWithRetry(mockRandomString, mockAuthCodeFactory, mockAuthCodeRepository);
    }

    @Test
    public void testRun() throws Exception {
        AccessRequest accessRequest = new AccessRequest();

        String authorizationCode = "randomString";
        when(mockRandomString.run()).thenReturn(authorizationCode);
        AuthCode authCode = new AuthCode();
        when(mockAuthCodeFactory.makeAuthCode(accessRequest, authorizationCode, subject.getSecondsToExpiration())).thenReturn(authCode);

        String actual = subject.run(accessRequest, 1);
        assertThat(actual, is(authorizationCode));
        verify(mockAuthCodeRepository).insert(authCode);
    }

    @Test
    public void testDuplicateFoundSuccessOnRetry() throws Exception {
        AccessRequest accessRequest = new AccessRequest();

        String authorizationCode = "randomString";
        when(mockRandomString.run()).thenReturn(authorizationCode);
        AuthCode authCode = new AuthCode();
        when(mockAuthCodeFactory.makeAuthCode(
                accessRequest,
                authorizationCode,
                subject.getSecondsToExpiration())
        ).thenReturn(authCode);

        doThrow(DuplicateRecordException.class)
        .doNothing().when(mockAuthCodeRepository).insert(authCode);

        String actual = subject.run(accessRequest, 1);
        assertThat(actual, is(authorizationCode));

        verify(mockAuthCodeRepository, times(2)).insert(authCode);
    }

    @Test(expected = AuthCodeInsertException.class)
    public void testReachesMaxRetries() throws Exception {
        AccessRequest accessRequest = new AccessRequest();

        String authorizationCode = "randomString";
        when(mockRandomString.run()).thenReturn(authorizationCode);
        AuthCode authCode = new AuthCode();
        when(mockAuthCodeFactory.makeAuthCode(accessRequest, authorizationCode, subject.getSecondsToExpiration())).thenReturn(authCode);

        doThrow(DuplicateRecordException.class)
        .doThrow(DuplicateRecordException.class)
        .doThrow(DuplicateRecordException.class).when(mockAuthCodeRepository).insert(authCode);

        subject.run(accessRequest, 1);
    }
}