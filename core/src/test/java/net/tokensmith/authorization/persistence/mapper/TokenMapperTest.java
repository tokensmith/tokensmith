package net.tokensmith.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.TestAppConfig;
import helper.fixture.persistence.LoadConfClientTokenReady;
import org.junit.Test;
import org.junit.runner.RunWith;
import net.tokensmith.repository.entity.*;
import net.tokensmith.repository.repo.*;
import net.tokensmith.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= TestAppConfig.class, loader= AnnotationConfigContextLoader.class)
@Transactional
public class TokenMapperTest {

    @Autowired
    private LoadConfClientTokenReady loadConfClientTokenReady;
    @Autowired
    private RandomString randomString;
    @Autowired
    private AuthCodeTokenRepository authCodeTokenRepository;
    @Autowired
    private ResourceOwnerTokenRepository resourceOwnerTokenRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private TokenMapper subject;

    @Test
    public void insert() throws Exception {
        Client client = FixtureFactory.makeCodeClientWithOpenIdScopes();
        clientRepository.insert(client);

        String accessToken = "accessToken";
        Token token = FixtureFactory.makeOpenIdToken(accessToken, client.getId(), new ArrayList<>());
        subject.insert(token);

        assertThat(token.getCreatedAt(), is(notNullValue()));
        assertThat(token.getLeadAuthTime(), is(notNullValue()));
    }

    @Test
    public void insertDuplicateShouldThrowDuplicateKeyException() throws Exception {
        Client client = FixtureFactory.makeCodeClientWithOpenIdScopes();
        clientRepository.insert(client);

        String accessToken = "accessToken";
        Token token = FixtureFactory.makeOpenIdToken(accessToken, client.getId(), new ArrayList<>());
        subject.insert(token);

        DuplicateKeyException actual = null;
        token.setId(UUID.randomUUID());
        try {
            subject.insert(token);
        } catch(DuplicateKeyException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage().contains("Detail: Key (active_token)"), is(true));
    }

    public AuthCode prepare() throws Exception {
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfClientTokenReady.run(true, false, plainTextAuthCode);

        // token to revoke.
        String accessToken = randomString.run();
        Token tokenToRevoke = FixtureFactory.makeOpenIdToken(accessToken, authCode.getAccessRequest().getClientId(), new ArrayList<>());
        subject.insert(tokenToRevoke);

        AuthCodeToken authCodeToken = new AuthCodeToken();
        authCodeToken.setId(UUID.randomUUID());
        authCodeToken.setTokenId(tokenToRevoke.getId());
        authCodeToken.setAuthCodeId(authCode.getId());

        // token should be associated to resource owner.
        ResourceOwner resourceOwner = new ResourceOwner();
        resourceOwner.setId(authCode.getAccessRequest().getResourceOwnerId());

        ResourceOwnerToken rot = new ResourceOwnerToken();
        rot.setId(UUID.randomUUID());
        rot.setResourceOwner(resourceOwner);
        rot.setToken(tokenToRevoke);
        resourceOwnerTokenRepository.insert(rot);

        authCodeTokenRepository.insert(authCodeToken);

        return authCode;
    }

    @Test
    public void revokeByAuthCodeIdShouldBeOk() throws Exception {
        // begin prepare db for test.
        AuthCode authCodeToRevoke = prepare();
        AuthCode authCodeToNotRevoke = prepare();
        // end prepare db for test.

        subject.revokeByAuthCodeId(authCodeToRevoke.getId());

        Token actual = subject.getByAuthCodeId(authCodeToRevoke.getId());
        assertThat(actual.isRevoked(), is(true));

        // make sure it didn't revoke other tokens.
        Token notRevoked = subject.getByAuthCodeId(authCodeToNotRevoke.getId());
        assertThat(notRevoked.isRevoked(), is(false));

    }

    @Test
    public void getByAuthCodeIdShouldBeOk() throws Exception {
        // begin prepare db for test.
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfClientTokenReady.run(true, false, plainTextAuthCode);

        String accessToken = "access-token";
        Token token = FixtureFactory.makeOpenIdToken(accessToken, authCode.getAccessRequest().getClientId(), new ArrayList<>());
        subject.insert(token);

        AuthCodeToken authCodeToken = new AuthCodeToken();
        authCodeToken.setId(UUID.randomUUID());
        authCodeToken.setAuthCodeId(authCode.getId());
        authCodeToken.setTokenId(token.getId());
        authCodeTokenRepository.insert(authCodeToken);
        // end prepare db for test.

        Token actual = subject.getByAuthCodeId(authCode.getId());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(token.getId()));
        assertThat(actual.getToken(), is(token.getToken()));
        assertThat(actual.isRevoked(), is(false));
        assertThat(actual.getGrantType(), is(token.getGrantType()));
        assertThat(actual.getClientId(), is(authCode.getAccessRequest().getClientId()));
        assertThat(token.getLeadAuthTime(), is(notNullValue()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
        assertThat(actual.getExpiresAt().toEpochSecond(), is(token.getExpiresAt().toEpochSecond()));
        assertThat(actual.getNonce(), is(notNullValue()));
        assertThat(actual.getNonce().isPresent(), is(true));
        assertThat(actual.getNonce().get(), is("nonce-123"));
    }

    @Test
    public void revokeByIdShouldBeOk() throws Exception {
        Client client = FixtureFactory.makeCodeClientWithOpenIdScopes();
        clientRepository.insert(client);

        String accessToken = "access-token";
        Token token = FixtureFactory.makeOpenIdToken(accessToken, client.getId(), new ArrayList<>());
        subject.insert(token);

        assertThat(token.isRevoked(), is(false));

        subject.revokeById(token.getId());

        Token actual = subject.getById(token.getId());

        assertThat(actual.getId(), is(token.getId()));
        assertThat(actual.getToken(), is(token.getToken()));
        assertThat(actual.isRevoked(), is(true));
        assertThat(actual.getGrantType(), is(token.getGrantType()));
        assertThat(actual.getClientId(), is(client.getId()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
        assertThat(actual.getExpiresAt().toEpochSecond(), is(token.getExpiresAt().toEpochSecond()));
    }

    @Test
    public void updateExpiresAtByAccessTokenShouldBeOk() throws Exception {
        Client client = FixtureFactory.makeCodeClientWithOpenIdScopes();
        clientRepository.insert(client);

        String accessToken = "access-token";
        Token token = FixtureFactory.makeOpenIdToken(accessToken, client.getId(), new ArrayList<>());
        subject.insert(token);

        assertThat(token.isRevoked(), is(false));

        OffsetDateTime expiresAt = token.getExpiresAt().minusDays(1);
        String hashedAccessToken = new String(token.getToken());
        subject.updateExpiresAtByAccessToken(expiresAt, hashedAccessToken);

        Token actual = subject.getById(token.getId());

        assertThat(actual.getId(), is(token.getId()));
        assertThat(actual.getToken(), is(token.getToken()));
        assertThat(actual.isRevoked(), is(false));
        assertThat(actual.getGrantType(), is(token.getGrantType()));
        assertThat(actual.getClientId(), is(client.getId()));
        assertThat(token.getLeadAuthTime(), is(notNullValue()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
        assertThat(actual.getExpiresAt().toEpochSecond(), is(expiresAt.toEpochSecond()));
    }

    @Test
    public void revokeActiveShouldRevoke() throws Exception {
        AuthCode authCodeToNotRevoke = prepare();
        AuthCode authCode = prepare();

        // make sure its not revoked.
        Token token = subject.getByAuthCodeId(authCode.getId());
        assertThat(token.isRevoked(), is(false));

        // revoke it.
        subject.revokeActive(authCode.getAccessRequest().getResourceOwnerId());

        Token actual = subject.getByAuthCodeId(authCode.getId());
        assertThat(actual.isRevoked(), is(true));

        // make sure it didn't revoke other resource owner tokens.
        Token tokenNotRevoked = subject.getByAuthCodeId(authCodeToNotRevoke.getId());
        assertThat(tokenNotRevoked.isRevoked(), is(false));
    }
}
