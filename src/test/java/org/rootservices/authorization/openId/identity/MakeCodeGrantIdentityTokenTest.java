package org.rootservices.authorization.openId.identity;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.openId.identity.exception.IdTokenException;
import org.rootservices.authorization.openId.identity.exception.KeyNotFoundException;
import org.rootservices.authorization.openId.identity.exception.AccessRequestNotFoundException;
import org.rootservices.authorization.openId.identity.exception.ProfileNotFoundException;
import org.rootservices.authorization.openId.identity.entity.IdToken;
import org.rootservices.authorization.openId.identity.factory.IdTokenFactory;
import org.rootservices.authorization.openId.identity.translator.PrivateKeyTranslator;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.*;
import org.rootservices.authorization.security.HashTextStaticSalt;
import org.rootservices.jwt.SecureJwtEncoder;
import org.rootservices.jwt.config.AppFactory;
import org.rootservices.jwt.entity.jwk.RSAKeyPair;
import org.rootservices.jwt.entity.jwt.header.Algorithm;
import org.rootservices.jwt.serializer.exception.JwtToJsonException;
import org.rootservices.jwt.signature.signer.factory.exception.InvalidAlgorithmException;
import org.rootservices.jwt.signature.signer.factory.exception.InvalidJsonWebKeyException;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 2/19/16.
 */
public class MakeCodeGrantIdentityTokenTest {

    private MakeCodeGrantIdentityToken subject;

    @Mock
    private HashTextStaticSalt mockHashText;
    @Mock
    private RsaPrivateKeyRepository mockRsaPrivateKeyRepository;
    @Mock
    private ResourceOwnerTokenRepository mockResourceOwnerTokenRepository;
    @Mock
    private PrivateKeyTranslator mockPrivateKeyTranslator;
    @Mock
    private AppFactory mockJwtAppFactory;
    @Mock
    private ProfileRepository mockProfileRepository;
    @Mock
    private IdTokenFactory mockIdTokenFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new MakeCodeGrantIdentityToken(
                mockHashText,
                mockRsaPrivateKeyRepository,
                mockResourceOwnerTokenRepository,
                mockPrivateKeyTranslator,
                mockJwtAppFactory,
                mockProfileRepository,
                mockIdTokenFactory
        );
    }

    @Test
    public void makeShouldReturnEncodedJwt() throws Exception {
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        ResourceOwnerToken rot = FixtureFactory.makeResourceOwnerToken();
        Profile profile = FixtureFactory.makeProfile(rot.getResourceOwner());
        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();
        RSAKeyPair keyPair = FixtureFactory.makeRSAKeyPair();

        List<String> scopesForIdToken = rot.getToken().getTokenScopes().stream()
                .map(item -> item.getScope().getName())
                .collect(Collectors.toList());

        IdToken idToken = new IdToken();
        SecureJwtEncoder mockSecureJwtEncoder = mock(SecureJwtEncoder.class);
        String expected = "some-compact-jwt";

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerTokenRepository.getByAccessToken(hashedAccessToken))
                .thenReturn(rot);

        when(mockProfileRepository.getByResourceOwnerId(rot.getResourceOwner().getId()))
                .thenReturn(profile);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        when(mockJwtAppFactory.secureJwtEncoder(Algorithm.RS256, keyPair))
                .thenReturn(mockSecureJwtEncoder);

        when(mockIdTokenFactory.make(scopesForIdToken, profile))
                .thenReturn(idToken);

        when(mockSecureJwtEncoder.encode(idToken))
                .thenReturn("some-compact-jwt");

        String actual = subject.make(accessToken);

        assertThat(actual, is(expected));

    }

    @Test(expected = AccessRequestNotFoundException.class)
    public void makeShouldThrowAccessRequestNotFoundException() throws Exception {
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerTokenRepository.getByAccessToken(hashedAccessToken))
                .thenThrow(RecordNotFoundException.class);

        subject.make(accessToken);
    }

    @Test(expected = KeyNotFoundException.class)
    public void makeShouldThrowKeyNotFoundException() throws Exception {
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        ResourceOwnerToken rot = FixtureFactory.makeResourceOwnerToken();
        Profile profile = FixtureFactory.makeProfile(rot.getResourceOwner());

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerTokenRepository.getByAccessToken(hashedAccessToken))
                .thenReturn(rot);

        when(mockProfileRepository.getByResourceOwnerId(rot.getResourceOwner().getId()))
                .thenReturn(profile);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenThrow(KeyNotFoundException.class);

        subject.make(accessToken);

    }

    @Test(expected = ProfileNotFoundException.class)
    public void makeShouldThrowProfileNotFoundException() throws Exception {
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        ResourceOwnerToken rot = FixtureFactory.makeResourceOwnerToken();
        Profile profile = FixtureFactory.makeProfile(rot.getResourceOwner());
        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerTokenRepository.getByAccessToken(hashedAccessToken))
                .thenReturn(rot);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockProfileRepository.getByResourceOwnerId(rot.getResourceOwner().getId()))
                .thenThrow(ProfileNotFoundException.class);

        subject.make(accessToken);

    }

    @Test(expected = IdTokenException.class)
    public void makeInvalidAlgorithmExceptionShouldThrowIdTokenException() throws Exception {
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        ResourceOwnerToken rot = FixtureFactory.makeResourceOwnerToken();
        Profile profile = FixtureFactory.makeProfile(rot.getResourceOwner());
        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();
        RSAKeyPair keyPair = FixtureFactory.makeRSAKeyPair();

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerTokenRepository.getByAccessToken(hashedAccessToken))
                .thenReturn(rot);

        when(mockProfileRepository.getByResourceOwnerId(rot.getResourceOwner().getId()))
                .thenReturn(profile);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        when(mockJwtAppFactory.secureJwtEncoder(Algorithm.RS256, keyPair))
                .thenThrow(InvalidAlgorithmException.class);

        subject.make(accessToken);

    }

    @Test(expected = IdTokenException.class)
    public void makeInvalidJsonWebKeyExceptionShouldThrowIdTokenException() throws Exception{
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        ResourceOwnerToken rot = FixtureFactory.makeResourceOwnerToken();
        Profile profile = FixtureFactory.makeProfile(rot.getResourceOwner());
        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();
        RSAKeyPair keyPair = FixtureFactory.makeRSAKeyPair();

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerTokenRepository.getByAccessToken(hashedAccessToken))
                .thenReturn(rot);

        when(mockProfileRepository.getByResourceOwnerId(rot.getResourceOwner().getId()))
                .thenReturn(profile);

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

        ResourceOwnerToken rot = FixtureFactory.makeResourceOwnerToken();
        Profile profile = FixtureFactory.makeProfile(rot.getResourceOwner());
        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();
        RSAKeyPair keyPair = FixtureFactory.makeRSAKeyPair();

        List<String> scopesForIdToken = rot.getToken().getTokenScopes().stream()
                .map(item -> item.getScope().getName())
                .collect(Collectors.toList());

        IdToken idToken = new IdToken();
        SecureJwtEncoder mockSecureJwtEncoder = mock(SecureJwtEncoder.class);
        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerTokenRepository.getByAccessToken(hashedAccessToken))
                .thenReturn(rot);

        when(mockProfileRepository.getByResourceOwnerId(rot.getResourceOwner().getId()))
                .thenReturn(profile);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        when(mockJwtAppFactory.secureJwtEncoder(Algorithm.RS256, keyPair))
                .thenReturn(mockSecureJwtEncoder);

        when(mockIdTokenFactory.make(scopesForIdToken, profile))
                .thenReturn(idToken);

        when(mockSecureJwtEncoder.encode(idToken))
                .thenThrow(JwtToJsonException.class);

        subject.make(accessToken);
    }
}