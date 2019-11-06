package net.tokensmith.authorization.openId.identity;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenClaims;
import net.tokensmith.authorization.openId.identity.entity.IdToken;
import net.tokensmith.authorization.openId.identity.exception.KeyNotFoundException;
import net.tokensmith.authorization.openId.identity.exception.ResourceOwnerNotFoundException;
import net.tokensmith.authorization.openId.identity.factory.IdTokenFactory;
import net.tokensmith.authorization.openId.identity.translator.PrivateKeyTranslator;
import net.tokensmith.repository.entity.Profile;
import net.tokensmith.repository.entity.RSAPrivateKey;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.repository.entity.Token;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.ResourceOwnerRepository;
import net.tokensmith.repository.repo.RsaPrivateKeyRepository;
import net.tokensmith.authorization.security.ciphers.HashToken;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.RSAKeyPair;


import java.io.ByteArrayOutputStream;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 2/19/16.
 */
public class MakeUserInfoIdentityTokenTest {

    private MakeUserInfoIdentityToken subject;

    @Mock
    private HashToken mockHashToken;
    @Mock
    private ResourceOwnerRepository mockResourceOwnerRepository;
    @Mock
    private RsaPrivateKeyRepository mockRsaPrivateKeyRepository;
    @Mock
    private PrivateKeyTranslator mockPrivateKeyTranslator;
    @Mock
    private IdTokenFactory mockIdTokenFactory;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new MakeUserInfoIdentityToken(
                mockHashToken,
                mockResourceOwnerRepository,
                mockRsaPrivateKeyRepository,
                mockPrivateKeyTranslator,
                new JwtAppFactory(),
                mockIdTokenFactory,
                FixtureFactory.makeSecureRedirectUri().toString()
        );
    }

    @Test
    public void makeWhenNoLeadTokenShouldReturnEncodedJwt() throws Exception {
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(ro.getId());
        ro.setProfile(profile);

        UUID clientId = UUID.randomUUID();
        Token token = FixtureFactory.makeOpenIdToken(accessToken, clientId, new ArrayList<>());
        token.setCreatedAt(OffsetDateTime.now());
        ro.getTokens().add(token);

        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();
        RSAKeyPair keyPair = FixtureFactory.makeRSAKeyPair();

        List<String> scopesForIdToken = ro.getTokens().get(0).getTokenScopes().stream()
                .map(item -> item.getScope().getName())
                .collect(Collectors.toList());

        IdToken idToken = new IdToken();

        when(mockHashToken.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerRepository.getByAccessTokenWithProfileAndTokens(hashedAccessToken))
                .thenReturn(ro);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        ArgumentCaptor<TokenClaims> tcArgumentCaptor = ArgumentCaptor.forClass(TokenClaims.class);
        when(mockIdTokenFactory.make(tcArgumentCaptor.capture(), eq(scopesForIdToken), eq(ro)))
                .thenReturn(idToken);

        ByteArrayOutputStream actual = subject.make(accessToken);

        assertThat(actual, is(notNullValue()));

        List<String> expectedAudience = token.getAudience().stream()
                .map(i->i.getId().toString())
                .collect(Collectors.toList());

        assertThat(tcArgumentCaptor.getValue().getIssuer(), is(FixtureFactory.makeSecureRedirectUri().toString()));
        assertThat(tcArgumentCaptor.getValue().getAudience(), is(expectedAudience));
        assertThat(tcArgumentCaptor.getValue().getAuthTime(), is(token.getCreatedAt().toEpochSecond()));
        assertThat(tcArgumentCaptor.getValue().getExpirationTime(), is(token.getExpiresAt().toEpochSecond()));
        assertThat(tcArgumentCaptor.getValue().getIssuedAt(), is(token.getCreatedAt().toEpochSecond()));

    }

    @Test
    public void makeWhenLeadTokenShouldReturnEncodedJwt() throws Exception {
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(ro.getId());
        ro.setProfile(profile);

        UUID clientId = UUID.randomUUID();
        Token token = FixtureFactory.makeOpenIdToken(accessToken, clientId, new ArrayList<>());
        token.setCreatedAt(OffsetDateTime.now());
        ro.getTokens().add(token);

        // lead token
        Token leadToken = FixtureFactory.makeOpenIdToken(accessToken, clientId, new ArrayList<>());
        leadToken.setCreatedAt(token.getCreatedAt().minusSeconds(1));

        token.setLeadToken(leadToken);

        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();
        RSAKeyPair keyPair = FixtureFactory.makeRSAKeyPair();

        List<String> scopesForIdToken = ro.getTokens().get(0).getTokenScopes().stream()
                .map(item -> item.getScope().getName())
                .collect(Collectors.toList());

        IdToken idToken = new IdToken();

        when(mockHashToken.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerRepository.getByAccessTokenWithProfileAndTokens(hashedAccessToken))
                .thenReturn(ro);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        ArgumentCaptor<TokenClaims> tcArgumentCaptor = ArgumentCaptor.forClass(TokenClaims.class);
        when(mockIdTokenFactory.make(tcArgumentCaptor.capture(), eq(scopesForIdToken), eq(ro)))
                .thenReturn(idToken);

        ByteArrayOutputStream actual = subject.make(accessToken);

        assertThat(actual, is(notNullValue()));

        List<String> expectedAudience = token.getAudience().stream()
                .map(i->i.getId().toString())
                .collect(Collectors.toList());

        assertThat(tcArgumentCaptor.getValue().getIssuer(), is(FixtureFactory.makeSecureRedirectUri().toString()));
        assertThat(tcArgumentCaptor.getValue().getAudience(), is(expectedAudience));
        assertThat(tcArgumentCaptor.getValue().getAuthTime(), is(leadToken.getCreatedAt().toEpochSecond()));
        assertThat(tcArgumentCaptor.getValue().getExpirationTime(), is(token.getExpiresAt().toEpochSecond()));
        assertThat(tcArgumentCaptor.getValue().getIssuedAt(), is(token.getCreatedAt().toEpochSecond()));

    }

    @Test(expected = ResourceOwnerNotFoundException.class)
    public void makeShouldThrowResourceOwnerNotFoundException() throws Exception {
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        when(mockHashToken.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerRepository.getByAccessTokenWithProfileAndTokens(hashedAccessToken))
                .thenThrow(RecordNotFoundException.class);

        subject.make(accessToken);
    }

    @Test(expected = KeyNotFoundException.class)
    public void makeShouldThrowKeyNotFoundException() throws Exception {
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(ro.getId());
        ro.setProfile(profile);

        UUID clientId = UUID.randomUUID();
        Token token = FixtureFactory.makeOpenIdToken(accessToken, clientId, new ArrayList<>());
        ro.getTokens().add(token);

        when(mockHashToken.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerRepository.getByAccessTokenWithProfileAndTokens(hashedAccessToken))
                .thenReturn(ro);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenThrow(RecordNotFoundException.class);

        subject.make(accessToken);

    }

}