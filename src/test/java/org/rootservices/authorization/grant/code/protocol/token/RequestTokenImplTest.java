package org.rootservices.authorization.grant.code.protocol.token;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.authenticate.LoginConfidentialClient;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.exception.BaseInformException;
import org.rootservices.authorization.persistence.entity.AccessRequest;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;
import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.AccessRequestRepository;
import org.rootservices.authorization.persistence.repository.TokenRepository;
import org.rootservices.authorization.security.RandomString;

import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by tommackenzie on 6/2/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class RequestTokenImplTest {

    @Mock
    private LoginConfidentialClient mockLoginConfidentialClient;
    @Mock
    private AccessRequestRepository mockAccessRequestRepository;
    @Mock
    private RandomString mockRandomString;
    @Mock
    private MakeToken mockMakeToken;
    @Mock
    private TokenRepository mockTokenRepository;

    private RequestToken subject;

    @Before
    public void setUp() {
        subject = new RequestTokenImpl(
                mockLoginConfidentialClient,
                mockAccessRequestRepository,
                mockRandomString,
                mockMakeToken,
                mockTokenRepository
        );
    }

    @Test
    public void testRun() throws Exception {

        String password = "client-password";
        String code = "valid-authorization-code";

        Client client = FixtureFactory.makeClientWithScopes();
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);

        when(mockLoginConfidentialClient.run(client.getUuid(), password)).thenReturn(confidentialClient);

        AccessRequest accessRequest = FixtureFactory.makeAccessRequest(UUID.randomUUID());
        when(mockAccessRequestRepository.getByClientUUIDAndAuthCode(client.getUuid(), code)).thenReturn(accessRequest);

        when(mockRandomString.run()).thenReturn("random-string");

        Token token = FixtureFactory.makeToken(accessRequest.getAuthCodeUUID());
        when(mockMakeToken.run(accessRequest.getAuthCodeUUID(), "random-string")).thenReturn(token);
        when(mockMakeToken.getSecondsToExpiration()).thenReturn(3600);
        when(mockMakeToken.getTokenType()).thenReturn(TokenType.BEARER);

        TokenResponse actual = subject.run(client.getUuid(), password, code, Optional.of(client.getRedirectURI()));
        assertThat(actual).isNotNull();
        assertThat(actual.getToken()).isNotNull();
        assertThat(actual.getSecondsToExpiration()).isEqualTo(3600);
        assertThat(actual.getTokenType()).isEqualTo(TokenType.BEARER);

    }

    @Test
    public void testRunLoginClientFails() throws URISyntaxException, UnauthorizedException, RecordNotFoundException {

        String password = "client-password";
        String code = "valid-authorization-code";

        Client client = FixtureFactory.makeClientWithScopes();

        when(mockLoginConfidentialClient.run(client.getUuid(), password)).thenThrow(UnauthorizedException.class);

        TokenResponse actual = null;
        try {
            actual = subject.run(client.getUuid(), password, code, Optional.of(client.getRedirectURI()));
            fail("No exception was thrown. Expected UnauthorizedException");
        } catch (UnauthorizedException e) {
            verify(mockAccessRequestRepository, never()).getByClientUUIDAndAuthCode(client.getUuid(), code);
            verify(mockTokenRepository, never()).insert(any(Token.class));
        } catch (BaseInformException e) {
            fail("BaseInformException was thrown. Expected UnauthorizedException");
        }
        assertThat(actual).isNull();
    }

}