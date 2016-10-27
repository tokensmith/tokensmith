package org.rootservices.authorization.openId.identity;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenClaims;
import org.rootservices.authorization.openId.identity.entity.IdToken;
import org.rootservices.authorization.openId.identity.exception.IdTokenException;
import org.rootservices.authorization.openId.identity.exception.KeyNotFoundException;
import org.rootservices.authorization.openId.identity.exception.ProfileNotFoundException;
import org.rootservices.authorization.openId.identity.factory.IdTokenFactory;
import org.rootservices.authorization.openId.identity.translator.PrivateKeyTranslator;
import org.rootservices.authorization.persistence.entity.Profile;
import org.rootservices.authorization.persistence.entity.RSAPrivateKey;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ProfileRepository;
import org.rootservices.authorization.persistence.repository.RsaPrivateKeyRepository;
import org.rootservices.jwt.SecureJwtEncoder;
import org.rootservices.jwt.config.AppFactory;
import org.rootservices.jwt.entity.jwk.RSAKeyPair;
import org.rootservices.jwt.entity.jwt.header.Algorithm;
import org.rootservices.jwt.serializer.exception.JwtToJsonException;
import org.rootservices.jwt.signature.signer.factory.exception.InvalidAlgorithmException;
import org.rootservices.jwt.signature.signer.factory.exception.InvalidJsonWebKeyException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 9/5/16.
 */
public class MakeImplicitIdentityTokenTest {

    private MakeImplicitIdentityToken subject;
    @Mock
    private ProfileRepository mockProfileRepository;
    @Mock
    private MakeAccessTokenHash mockMakeAccessTokenHash;
    @Mock
    private IdTokenFactory mockIdTokenFactory;
    @Mock
    private RsaPrivateKeyRepository mockRsaPrivateKeyRepository;
    @Mock
    private PrivateKeyTranslator mockPrivateKeyTranslator;
    @Mock
    private AppFactory mockJwtAppFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new MakeImplicitIdentityToken(
                mockProfileRepository,
                mockMakeAccessTokenHash,
                mockIdTokenFactory,
                mockRsaPrivateKeyRepository,
                mockPrivateKeyTranslator,
                mockJwtAppFactory
        );
    }

    @Test
    public void makeShouldReturnEncodedJwt() throws Exception{

        String accessToken = "some-access-token";
        String nonce = "some-nonce";

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(resourceOwner);
        List<String> scopesForIdToken = new ArrayList<>();

        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());
        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);

        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();
        RSAKeyPair keyPair = FixtureFactory.makeRSAKeyPair();
        IdToken idToken = new IdToken();
        SecureJwtEncoder mockSecureJwtEncoder = mock(SecureJwtEncoder.class);
        String expected = "some-compact-jwt";

        when(mockProfileRepository.getByResourceOwnerId(resourceOwner.getId()))
                .thenReturn(profile);

        when(mockMakeAccessTokenHash.makeEncodedHash(accessToken))
                .thenReturn("accessTokenHash");

        when(mockIdTokenFactory.make("accessTokenHash", nonce, tc, scopesForIdToken, profile))
                .thenReturn(idToken);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        when(mockJwtAppFactory.secureJwtEncoder(Algorithm.RS256, keyPair))
                .thenReturn(mockSecureJwtEncoder);

        when(mockSecureJwtEncoder.encode(idToken))
                .thenReturn(expected);

        String actual = subject.makeForAccessToken(accessToken, nonce, tc, resourceOwner.getId(), scopesForIdToken);

        assertThat(actual, is(expected));
    }

    @Test(expected = ProfileNotFoundException.class)
    public void makeShouldThrowProfileNotFoundException() throws Exception {

        String accessToken = "some-access-token";
        String nonce = "some-nonce";

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        List<String> scopesForIdToken = new ArrayList<>();

        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());
        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);

        when(mockProfileRepository.getByResourceOwnerId(resourceOwner.getId()))
                .thenThrow(RecordNotFoundException.class);

        subject.makeForAccessToken(accessToken, nonce, tc, resourceOwner.getId(), scopesForIdToken);
    }

    @Test(expected = KeyNotFoundException.class)
    public void makeShouldThrowKeyNotFoundException() throws Exception {

        String accessToken = "some-access-token";
        String nonce = "some-nonce";

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(resourceOwner);
        List<String> scopesForIdToken = new ArrayList<>();

        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());
        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);

        IdToken idToken = new IdToken();

        when(mockProfileRepository.getByResourceOwnerId(resourceOwner.getId()))
                .thenReturn(profile);

        when(mockMakeAccessTokenHash.makeEncodedHash(accessToken))
                .thenReturn("accessTokenHash");

        when(mockIdTokenFactory.make("accessTokenHash", tc, scopesForIdToken, profile))
                .thenReturn(idToken);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenThrow(RecordNotFoundException.class);

        subject.makeForAccessToken(accessToken, nonce, tc, resourceOwner.getId(), scopesForIdToken);
    }

    @Test(expected = IdTokenException.class)
    public void makeWhenInvalidAlgorithmExceptionShouldThrowIdTokenException() throws Exception {

        String accessToken = "some-access-token";
        String nonce = "some-nonce";

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(resourceOwner);
        List<String> scopesForIdToken = new ArrayList<>();

        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());
        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);

        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();
        RSAKeyPair keyPair = FixtureFactory.makeRSAKeyPair();
        IdToken idToken = new IdToken();

        when(mockProfileRepository.getByResourceOwnerId(resourceOwner.getId()))
                .thenReturn(profile);

        when(mockMakeAccessTokenHash.makeEncodedHash(accessToken))
                .thenReturn("accessTokenHash");

        when(mockIdTokenFactory.make("accessTokenHash", tc, scopesForIdToken, profile))
                .thenReturn(idToken);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        when(mockJwtAppFactory.secureJwtEncoder(Algorithm.RS256, keyPair))
                .thenThrow(InvalidAlgorithmException.class);

        subject.makeForAccessToken(accessToken, nonce, tc, resourceOwner.getId(), scopesForIdToken);
    }

    @Test(expected = IdTokenException.class)
    public void makeWhenInvalidJsonWebKeyExceptionShouldThrowIdTokenException() throws Exception {

        String accessToken = "some-access-token";
        String nonce = "some-nonce";

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(resourceOwner);
        List<String> scopesForIdToken = new ArrayList<>();

        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());
        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);

        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();
        RSAKeyPair keyPair = FixtureFactory.makeRSAKeyPair();
        IdToken idToken = new IdToken();

        when(mockProfileRepository.getByResourceOwnerId(resourceOwner.getId()))
                .thenReturn(profile);

        when(mockMakeAccessTokenHash.makeEncodedHash(accessToken))
                .thenReturn("accessTokenHash");

        when(mockIdTokenFactory.make("accessTokenHash", tc, scopesForIdToken, profile))
                .thenReturn(idToken);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        when(mockJwtAppFactory.secureJwtEncoder(Algorithm.RS256, keyPair))
                .thenThrow(InvalidJsonWebKeyException.class);

        subject.makeForAccessToken(accessToken, nonce, tc, resourceOwner.getId(), scopesForIdToken);
    }

    @Test(expected = IdTokenException.class)
    public void makeWhenJwtToJsonExceptionShouldThrowIdTokenException() throws Exception {
        String accessToken = "some-access-token";
        String nonce = "some-nonce";

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(resourceOwner);
        List<String> scopesForIdToken = new ArrayList<>();

        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());
        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);

        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();
        RSAKeyPair keyPair = FixtureFactory.makeRSAKeyPair();
        IdToken idToken = new IdToken();
        SecureJwtEncoder mockSecureJwtEncoder = mock(SecureJwtEncoder.class);

        when(mockProfileRepository.getByResourceOwnerId(resourceOwner.getId()))
                .thenReturn(profile);

        when(mockMakeAccessTokenHash.makeEncodedHash(accessToken))
                .thenReturn("accessTokenHash");

        when(mockIdTokenFactory.make("accessTokenHash", nonce, tc, scopesForIdToken, profile))
                .thenReturn(idToken);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        when(mockJwtAppFactory.secureJwtEncoder(Algorithm.RS256, keyPair))
                .thenReturn(mockSecureJwtEncoder);

        when(mockSecureJwtEncoder.encode(idToken))
                .thenThrow(JwtToJsonException.class);

        subject.makeForAccessToken(accessToken, nonce, tc, resourceOwner.getId(), scopesForIdToken);
    }

    @Test
    public void makeIdentityOnlyShouldReturnEncodedJwt() throws Exception {
        String nonce = "some-nonce";

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(resourceOwner);

        List<String> scopesForIdToken = new ArrayList<>();
        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());
        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);

        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();
        RSAKeyPair keyPair = FixtureFactory.makeRSAKeyPair();
        IdToken idToken = new IdToken();
        SecureJwtEncoder mockSecureJwtEncoder = mock(SecureJwtEncoder.class);
        String expected = "some-compact-jwt";

        when(mockProfileRepository.getByResourceOwnerId(resourceOwner.getId()))
                .thenReturn(profile);

        when(mockIdTokenFactory.make(nonce, tc, scopesForIdToken, profile))
                .thenReturn(idToken);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        when(mockJwtAppFactory.secureJwtEncoder(Algorithm.RS256, keyPair))
                .thenReturn(mockSecureJwtEncoder);

        when(mockSecureJwtEncoder.encode(idToken))
                .thenReturn(expected);

        String actual = subject.makeIdentityOnly(nonce, tc, resourceOwner.getId(), scopesForIdToken);

        assertThat(actual, is(expected));
    }

    @Test(expected = ProfileNotFoundException.class)
    public void makeIdentityOnlyShouldThrowProfileNotFoundException() throws Exception {

        String nonce = "some-nonce";

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        List<String> scopesForIdToken = new ArrayList<>();

        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());
        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);

        when(mockProfileRepository.getByResourceOwnerId(resourceOwner.getId()))
                .thenThrow(RecordNotFoundException.class);

        subject.makeIdentityOnly(nonce, tc, resourceOwner.getId(), scopesForIdToken);
    }

    @Test(expected = KeyNotFoundException.class)
    public void makeIdentityOnlyShouldThrowKeyNotFoundException() throws Exception {

        String nonce = "some-nonce";

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(resourceOwner);

        List<String> scopesForIdToken = new ArrayList<>();
        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());
        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);

        IdToken idToken = new IdToken();

        when(mockProfileRepository.getByResourceOwnerId(resourceOwner.getId()))
                .thenReturn(profile);

        when(mockIdTokenFactory.make(nonce, tc, scopesForIdToken, profile))
                .thenReturn(idToken);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenThrow(RecordNotFoundException.class);

        subject.makeIdentityOnly(nonce, tc, resourceOwner.getId(), scopesForIdToken);
    }

    @Test(expected = IdTokenException.class)
    public void makeIdentityOnlyWhenInvalidAlgorithmExceptionShouldThrowIdTokenException() throws Exception {

        String nonce = "some-nonce";

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(resourceOwner);

        List<String> scopesForIdToken = new ArrayList<>();
        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());
        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);

        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();
        RSAKeyPair keyPair = FixtureFactory.makeRSAKeyPair();
        IdToken idToken = new IdToken();

        when(mockProfileRepository.getByResourceOwnerId(resourceOwner.getId()))
                .thenReturn(profile);

        when(mockIdTokenFactory.make(nonce, tc, scopesForIdToken, profile))
                .thenReturn(idToken);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        when(mockJwtAppFactory.secureJwtEncoder(Algorithm.RS256, keyPair))
                .thenThrow(InvalidAlgorithmException.class);

        subject.makeIdentityOnly(nonce, tc, resourceOwner.getId(), scopesForIdToken);
    }

    @Test(expected = IdTokenException.class)
    public void makeIdentityOnlyWhenInvalidJsonWebKeyExceptionShouldThrowIdTokenException() throws Exception {

        String nonce = "some-nonce";

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(resourceOwner);

        List<String> scopesForIdToken = new ArrayList<>();
        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());
        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);

        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();
        RSAKeyPair keyPair = FixtureFactory.makeRSAKeyPair();
        IdToken idToken = new IdToken();

        when(mockProfileRepository.getByResourceOwnerId(resourceOwner.getId()))
                .thenReturn(profile);

        when(mockIdTokenFactory.make(nonce, tc, scopesForIdToken, profile))
                .thenReturn(idToken);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        when(mockJwtAppFactory.secureJwtEncoder(Algorithm.RS256, keyPair))
                .thenThrow(InvalidJsonWebKeyException.class);

        subject.makeIdentityOnly(nonce, tc, resourceOwner.getId(), scopesForIdToken);
    }

    @Test(expected = IdTokenException.class)
    public void makeIdentityOnlyWhenJwtToJsonExceptionShouldThrowIdTokenException() throws Exception {

        String nonce = "some-nonce";

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(resourceOwner);

        List<String> scopesForIdToken = new ArrayList<>();
        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());
        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);

        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();
        RSAKeyPair keyPair = FixtureFactory.makeRSAKeyPair();
        IdToken idToken = new IdToken();
        SecureJwtEncoder mockSecureJwtEncoder = mock(SecureJwtEncoder.class);

        when(mockProfileRepository.getByResourceOwnerId(resourceOwner.getId()))
                .thenReturn(profile);

        when(mockIdTokenFactory.make(nonce, tc, scopesForIdToken, profile))
                .thenReturn(idToken);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        when(mockJwtAppFactory.secureJwtEncoder(Algorithm.RS256, keyPair))
                .thenReturn(mockSecureJwtEncoder);

        when(mockSecureJwtEncoder.encode(idToken))
                .thenThrow(JwtToJsonException.class);

        subject.makeIdentityOnly(nonce, tc, resourceOwner.getId(), scopesForIdToken);
    }
}