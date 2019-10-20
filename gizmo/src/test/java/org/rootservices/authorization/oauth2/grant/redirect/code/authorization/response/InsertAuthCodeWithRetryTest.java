package org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.factory.AuthCodeFactory;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.exception.AuthCodeInsertException;
import org.rootservices.authorization.persistence.entity.AccessRequest;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.persistence.entity.Configuration;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.AuthCodeRepository;
import org.rootservices.authorization.persistence.repository.ConfigurationRepository;
import org.rootservices.authorization.security.RandomString;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;


public class InsertAuthCodeWithRetryTest {

    private InsertAuthCodeWithRetry subject;
    @Mock
    private ConfigurationRepository mockConfigurationRepository;
    @Mock
    private RandomString mockRandomString;
    @Mock
    private AuthCodeFactory mockAuthCodeFactory;
    @Mock
    private AuthCodeRepository mockAuthCodeRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new InsertAuthCodeWithRetry(mockConfigurationRepository, mockRandomString, mockAuthCodeFactory, mockAuthCodeRepository);
    }

    @Test
    public void testRun() throws Exception {
        AccessRequest accessRequest = new AccessRequest();

        Configuration configuration = FixtureFactory.makeConfiguration();
        when(mockConfigurationRepository.get()).thenReturn(configuration);

        String authorizationCode = "authorization-code";
        when(mockRandomString.run(configuration.getAuthorizationCodeSize())).thenReturn(authorizationCode);

        AuthCode authCode = new AuthCode();
        when(mockAuthCodeFactory.makeAuthCode(accessRequest, authorizationCode, configuration.getAuthorizationCodeSecondsToExpiry())).thenReturn(authCode);

        String actual = subject.run(accessRequest);
        assertThat(actual, is(authorizationCode));
        verify(mockAuthCodeRepository).insert(authCode);
    }

    @Test
    public void testDuplicateFoundSuccessOnRetry() throws Exception {
        AccessRequest accessRequest = new AccessRequest();

        Configuration configuration = FixtureFactory.makeConfiguration();
        when(mockConfigurationRepository.get()).thenReturn(configuration);

        String authorizationCode = "authorization-code";
        when(mockRandomString.run(configuration.getAuthorizationCodeSize())).thenReturn(authorizationCode);
        when(mockRandomString.run(configuration.getAuthorizationCodeSize()+1)).thenReturn(authorizationCode);

        AuthCode authCode = new AuthCode();
        when(mockAuthCodeFactory.makeAuthCode(
                accessRequest,
                authorizationCode,
                configuration.getAuthorizationCodeSecondsToExpiry()
        )).thenReturn(authCode);

        DuplicateRecordException dre = new DuplicateRecordException("test", null, Optional.of("code"));
        doThrow(dre)
        .doNothing()
        .when(mockAuthCodeRepository).insert(authCode);

        String actual = subject.run(accessRequest);
        assertThat(actual, is(authorizationCode));

        verify(mockAuthCodeRepository, times(2)).insert(authCode);

        verify(mockConfigurationRepository, times(1)).updateAuthorizationCodeSize(configuration.getId(), configuration.getAuthorizationCodeSize()+1);
    }

    @Test
    public void testReachesMaxRetries() throws Exception {
        AccessRequest accessRequest = new AccessRequest();

        Configuration configuration = FixtureFactory.makeConfiguration();
        when(mockConfigurationRepository.get()).thenReturn(configuration);

        String authorizationCode = "authorization-code";
        when(mockRandomString.run(configuration.getAuthorizationCodeSize())).thenReturn(authorizationCode);
        when(mockRandomString.run(configuration.getAuthorizationCodeSize()+1)).thenReturn(authorizationCode);

        AuthCode authCode = new AuthCode();
        when(mockAuthCodeFactory.makeAuthCode(accessRequest, authorizationCode, configuration.getAuthorizationCodeSecondsToExpiry())).thenReturn(authCode);

        DuplicateRecordException dre = new DuplicateRecordException("test", null, Optional.of("code"));

        doThrow(dre)
        .doThrow(dre)
        .when(mockAuthCodeRepository).insert(authCode);

        AuthCodeInsertException actual = null;
        try {
            subject.run(accessRequest);
        } catch (AuthCodeInsertException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));

        verify(mockConfigurationRepository, times(1)).updateAuthorizationCodeSize(configuration.getId(), configuration.getAuthorizationCodeSize()+1);
    }
}