package org.rootservices.authorization.openId.identity;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenClaims;
import org.rootservices.authorization.openId.identity.entity.IdToken;
import org.rootservices.authorization.openId.identity.exception.IdTokenException;
import org.rootservices.authorization.openId.identity.exception.KeyNotFoundException;
import org.rootservices.authorization.openId.identity.exception.ProfileNotFoundException;
import org.rootservices.authorization.openId.identity.exception.ResourceOwnerNotFoundException;
import org.rootservices.authorization.openId.identity.factory.IdTokenFactory;
import org.rootservices.authorization.openId.identity.translator.PrivateKeyTranslator;
import org.rootservices.authorization.persistence.entity.Profile;
import org.rootservices.authorization.persistence.entity.RSAPrivateKey;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ResourceOwnerRepository;
import org.rootservices.authorization.persistence.repository.RsaPrivateKeyRepository;
import org.rootservices.authorization.security.HashTextStaticSalt;
import org.rootservices.jwt.SecureJwtEncoder;
import org.rootservices.jwt.config.AppFactory;
import org.rootservices.jwt.entity.jwk.RSAKeyPair;
import org.rootservices.jwt.entity.jwt.header.Algorithm;
import org.rootservices.jwt.serializer.exception.JwtToJsonException;
import org.rootservices.jwt.signature.signer.factory.exception.InvalidAlgorithmException;
import org.rootservices.jwt.signature.signer.factory.exception.InvalidJsonWebKeyException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 2/19/16.
 */
public class MakeUserInfoIdentityTokenTest {

    private MakeUserInfoIdentityToken subject;

    @Mock
    private HashTextStaticSalt mockHashText;
    @Mock
    private ResourceOwnerRepository mockResourceOwnerRepository;
    @Mock
    private RsaPrivateKeyRepository mockRsaPrivateKeyRepository;
    @Mock
    private PrivateKeyTranslator mockPrivateKeyTranslator;
    @Mock
    private AppFactory mockJwtAppFactory;
    @Mock
    private IdTokenFactory mockIdTokenFactory;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new MakeUserInfoIdentityToken(
                mockHashText,
                mockResourceOwnerRepository,
                mockRsaPrivateKeyRepository,
                mockPrivateKeyTranslator,
                mockJwtAppFactory,
                mockIdTokenFactory,
                FixtureFactory.makeSecureRedirectUri().toString()
        );
    }

    @Test
    public void makeShouldReturnEncodedJwt() throws Exception {
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
        SecureJwtEncoder mockSecureJwtEncoder = mock(SecureJwtEncoder.class);
        String expected = "some-compact-jwt";

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerRepository.getByAccessTokenWithProfileAndTokens(hashedAccessToken))
                .thenReturn(ro);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        when(mockJwtAppFactory.secureJwtEncoder(Algorithm.RS256, keyPair))
                .thenReturn(mockSecureJwtEncoder);

        ArgumentCaptor<TokenClaims> tcArgumentCaptor = ArgumentCaptor.forClass(TokenClaims.class);
        when(mockIdTokenFactory.make(tcArgumentCaptor.capture(), eq(scopesForIdToken), eq(ro)))
                .thenReturn(idToken);

        when(mockSecureJwtEncoder.encode(idToken))
                .thenReturn("some-compact-jwt");

        String actual = subject.make(accessToken);

        assertThat(actual, is(expected));

        List<String> expectedAudience = token.getAudience().stream()
                .map(i->i.getId().toString())
                .collect(Collectors.toList());

        assertThat(tcArgumentCaptor.getValue().getIssuer(), is(FixtureFactory.makeSecureRedirectUri().toString()));
        assertThat(tcArgumentCaptor.getValue().getAudience(), is(expectedAudience));
        assertThat(tcArgumentCaptor.getValue().getAuthTime(), is(token.getCreatedAt().toEpochSecond()));
        assertThat(tcArgumentCaptor.getValue().getExpirationTime(), is(token.getExpiresAt().toEpochSecond()));
        assertThat(tcArgumentCaptor.getValue().getIssuedAt(), is(token.getCreatedAt().toEpochSecond()));

    }

    @Test(expected = ResourceOwnerNotFoundException.class)
    public void makeShouldThrowResourceOwnerNotFoundException() throws Exception {
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerRepository.getByAccessTokenWithProfileAndTokens(hashedAccessToken))
                .thenThrow(RecordNotFoundException.class);

        subject.make(accessToken);
    }

    @Test(expected = ProfileNotFoundException.class)
    public void makeShouldThrowProfileNotFoundException() throws Exception {
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        ro.setProfile(null);

        UUID clientId = UUID.randomUUID();
        Token token = FixtureFactory.makeOpenIdToken(accessToken, clientId, new ArrayList<>());
        ro.getTokens().add(token);

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerRepository.getByAccessTokenWithProfileAndTokens(hashedAccessToken))
                .thenReturn(ro);

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

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerRepository.getByAccessTokenWithProfileAndTokens(hashedAccessToken))
                .thenReturn(ro);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenThrow(KeyNotFoundException.class);

        subject.make(accessToken);

    }

    @Test(expected = IdTokenException.class)
    public void makeInvalidAlgorithmExceptionShouldThrowIdTokenException() throws Exception {
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(ro.getId());
        ro.setProfile(profile);

        UUID clientId = UUID.randomUUID();
        Token token = FixtureFactory.makeOpenIdToken(accessToken, clientId, new ArrayList<>());
        token.setCreatedAt(OffsetDateTime.now());
        ro.getTokens().add(token);

        List<String> scopesForIdToken = ro.getTokens().get(0).getTokenScopes().stream()
                .map(item -> item.getScope().getName())
                .collect(Collectors.toList());

        IdToken idToken = new IdToken();

        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();
        RSAKeyPair keyPair = FixtureFactory.makeRSAKeyPair();

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerRepository.getByAccessTokenWithProfileAndTokens(hashedAccessToken))
                .thenReturn(ro);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        ArgumentCaptor<TokenClaims> tcArgumentCaptor = ArgumentCaptor.forClass(TokenClaims.class);
        when(mockIdTokenFactory.make(tcArgumentCaptor.capture(), eq(scopesForIdToken), eq(ro)))
                .thenReturn(idToken);

        when(mockJwtAppFactory.secureJwtEncoder(Algorithm.RS256, keyPair))
                .thenThrow(InvalidAlgorithmException.class);

        subject.make(accessToken);

    }

    @Test(expected = IdTokenException.class)
    public void makeInvalidJsonWebKeyExceptionShouldThrowIdTokenException() throws Exception{
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

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerRepository.getByAccessTokenWithProfileAndTokens(hashedAccessToken))
                .thenReturn(ro);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        when(mockJwtAppFactory.secureJwtEncoder(Algorithm.RS256, keyPair))
                .thenThrow(InvalidJsonWebKeyException.class);

        subject.make(accessToken);
    }

    @Test(expected = IdTokenException.class)
    public void makeWhenEncodeThrowsJwtToJsonExceptionShouldThrowIdTokenException() throws Exception {
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
        SecureJwtEncoder mockSecureJwtEncoder = mock(SecureJwtEncoder.class);
        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerRepository.getByAccessTokenWithProfileAndTokens(hashedAccessToken))
                .thenReturn(ro);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        when(mockJwtAppFactory.secureJwtEncoder(Algorithm.RS256, keyPair))
                .thenReturn(mockSecureJwtEncoder);

        ArgumentCaptor<TokenClaims> tcArgumentCaptor = ArgumentCaptor.forClass(TokenClaims.class);
        when(mockIdTokenFactory.make(tcArgumentCaptor.capture(), eq(scopesForIdToken), eq(ro)))
                .thenReturn(idToken);

        when(mockSecureJwtEncoder.encode(idToken))
                .thenThrow(JwtToJsonException.class);

        subject.make(accessToken);

        List<String> expectedAudience = token.getAudience().stream()
                .map(i->i.getId().toString())
                .collect(Collectors.toList());

        assertThat(tcArgumentCaptor.getValue().getIssuer(), is(FixtureFactory.makeSecureRedirectUri().toString()));
        assertThat(tcArgumentCaptor.getValue().getAudience(), is(expectedAudience));
        assertThat(tcArgumentCaptor.getValue().getAuthTime(), is(token.getCreatedAt().toEpochSecond()));
        assertThat(tcArgumentCaptor.getValue().getExpirationTime(), is(token.getExpiresAt().toEpochSecond()));
        assertThat(tcArgumentCaptor.getValue().getIssuedAt(), is(token.getCreatedAt().toEpochSecond()));
    }
}