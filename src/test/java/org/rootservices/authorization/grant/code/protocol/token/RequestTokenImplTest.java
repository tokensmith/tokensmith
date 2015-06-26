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
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.AuthCodeRepository;
import org.rootservices.authorization.persistence.repository.TokenRepository;
import org.rootservices.authorization.security.HashTextStaticSalt;
import org.rootservices.authorization.security.RandomString;

import java.net.URISyntaxException;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by tommackenzie on 6/2/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class RequestTokenImplTest {

    @Mock
    private LoginConfidentialClient mockLoginConfidentialClient;
    @Mock
    private HashTextStaticSalt mockHashText;
    @Mock
    private AuthCodeRepository mockAuthCodeRepository;
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
                mockHashText,
                mockAuthCodeRepository,
                mockRandomString,
                mockMakeToken,
                mockTokenRepository
        );
    }

    @Test
    public void testRun() throws Exception {

        Client client = FixtureFactory.makeClientWithScopes();
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);

        TokenInput tokenInput = new TokenInput();
        tokenInput.setClientUUID(client.getUuid().toString());
        tokenInput.setClientPassword("client-password");
        tokenInput.setCode("valid-authorization-code");
        tokenInput.setRedirectUri(client.getRedirectURI().toString());

        when(mockLoginConfidentialClient.run(client.getUuid(), tokenInput.getClientPassword())).thenReturn(confidentialClient);

        String hashedCode = "hased-valid-authorization-code";
        when(mockHashText.run(tokenInput.getCode())).thenReturn(hashedCode);

        UUID resourceOwnerUUID = UUID.randomUUID();
        AccessRequest accessRequest = FixtureFactory.makeAccessRequest(
                resourceOwnerUUID, client.getUuid()
        );
        AuthCode authCode = FixtureFactory.makeAuthCode(accessRequest);
        when(mockAuthCodeRepository.getByClientUUIDAndAuthCode(client.getUuid(), hashedCode)).thenReturn(authCode);

        when(mockRandomString.run()).thenReturn("random-string");

        Token token = FixtureFactory.makeToken(authCode.getUuid());
        when(mockMakeToken.run(authCode.getUuid(), "random-string")).thenReturn(token);
        when(mockMakeToken.getSecondsToExpiration()).thenReturn(3600);
        when(mockMakeToken.getTokenType()).thenReturn(TokenType.BEARER);

        TokenResponse actual = subject.run(tokenInput);
        assertThat(actual).isNotNull();
        assertThat(actual.getAccessToken()).isNotNull();
        assertThat(actual.getExpiresIn()).isEqualTo(3600);
        assertThat(actual.getTokenType()).isEqualTo(TokenType.BEARER.toString().toLowerCase());

    }

    @Test
    public void testRunLoginClientFails() throws URISyntaxException, UnauthorizedException, RecordNotFoundException {

        Client client = FixtureFactory.makeClientWithScopes();

        TokenInput tokenInput = new TokenInput();
        tokenInput.setClientUUID(client.getUuid().toString());
        tokenInput.setClientPassword("client-password");
        tokenInput.setCode("valid-authorization-code");
        tokenInput.setRedirectUri(client.getRedirectURI().toString());

        when(mockLoginConfidentialClient.run(client.getUuid(), tokenInput.getClientPassword())).thenThrow(UnauthorizedException.class);

        TokenResponse actual = null;
        try {
            actual = subject.run(tokenInput);
            fail("No exception was thrown. Expected UnauthorizedException");
        } catch (UnauthorizedException e) {
            verify(mockAuthCodeRepository, never()).getByClientUUIDAndAuthCode(client.getUuid(), tokenInput.getCode());
            verify(mockTokenRepository, never()).insert(any(Token.class));
        } catch (BaseInformException e) {
            fail("BaseInformException was thrown. Expected UnauthorizedException");
        }
        assertThat(actual).isNull();
    }

}