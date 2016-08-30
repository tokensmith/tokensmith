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
import org.rootservices.authorization.openId.identity.factory.IdTokenFactory;
import org.rootservices.authorization.openId.identity.entity.IdToken;
import org.rootservices.authorization.openId.identity.translator.PrivateKeyTranslator;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.*;
import org.rootservices.authorization.security.HashTextStaticSalt;
import org.rootservices.jwt.builder.SecureJwtBuilder;
import org.rootservices.jwt.config.AppFactory;
import org.rootservices.jwt.entity.jwk.RSAKeyPair;
import org.rootservices.jwt.entity.jwt.JsonWebToken;
import org.rootservices.jwt.entity.jwt.header.Algorithm;
import org.rootservices.jwt.serializer.JWTSerializer;
import org.rootservices.jwt.serializer.exception.JwtToJsonException;
import org.rootservices.jwt.signature.signer.factory.exception.InvalidAlgorithmException;
import org.rootservices.jwt.signature.signer.factory.exception.InvalidJsonWebKeyException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 2/19/16.
 */
public class BuildIdentityTokenTest {

    private BuildIdentityToken subject;

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
        subject = new BuildIdentityToken(
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
    public void buildShouldMakeJwt() throws Exception {
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        ResourceOwnerToken rot = FixtureFactory.makeResourceOwnerToken();
        Profile profile = FixtureFactory.makeProfile(rot.getResourceOwner());
        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();
        RSAKeyPair keyPair = FixtureFactory.makeRSAKeyPair();
        SecureJwtBuilder mockSecureJwtBuilder = mock(SecureJwtBuilder.class);
        IdToken idToken = new IdToken();
        JsonWebToken jsonWebToken = new JsonWebToken();
        JWTSerializer mockJwtSerializer = mock(JWTSerializer.class);
        String expected = "some-compact-jwt";

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerTokenRepository.getByAccessToken(hashedAccessToken))
                .thenReturn(rot);

        when(mockProfileRepository.getByResourceOwnerId(rot.getResourceOwner().getUuid()))
                .thenReturn(profile);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        when(mockJwtAppFactory.secureJwtBuilder(Algorithm.HS256, keyPair))
                .thenReturn(mockSecureJwtBuilder);

        when(mockIdTokenFactory.make(rot.getToken().getTokenScopes(), profile))
                .thenReturn(idToken);

        when(mockSecureJwtBuilder.build(idToken))
                .thenReturn(jsonWebToken);

        when(mockJwtAppFactory.jwtSerializer()).thenReturn(mockJwtSerializer);

        when(mockJwtSerializer.jwtToString(jsonWebToken))
                .thenReturn(expected);

        String actual = subject.build(accessToken);

        assertThat(actual, is(expected));

    }

    @Test(expected = AccessRequestNotFoundException.class)
    public void buildShouldThrowAccessRequestNotFoundException() throws Exception {
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerTokenRepository.getByAccessToken(hashedAccessToken))
                .thenThrow(RecordNotFoundException.class);

        subject.build(accessToken);
    }

    @Test(expected = KeyNotFoundException.class)
    public void buildShouldThrowKeyNotFoundException() throws Exception {
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        ResourceOwnerToken rot = FixtureFactory.makeResourceOwnerToken();
        Profile profile = FixtureFactory.makeProfile(rot.getResourceOwner());

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerTokenRepository.getByAccessToken(hashedAccessToken))
                .thenReturn(rot);

        when(mockProfileRepository.getByResourceOwnerId(rot.getResourceOwner().getUuid()))
                .thenReturn(profile);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenThrow(KeyNotFoundException.class);

        subject.build(accessToken);

    }

    @Test(expected = ProfileNotFoundException.class)
    public void buildShouldThrowProfileNotFoundException() throws Exception {
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

        when(mockProfileRepository.getByResourceOwnerId(rot.getResourceOwner().getUuid()))
                .thenThrow(ProfileNotFoundException.class);

        subject.build(accessToken);

    }

    @Test(expected = IdTokenException.class)
    public void buildInvalidAlgorithmExceptionShouldThrowIdTokenException() throws Exception {
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        ResourceOwnerToken rot = FixtureFactory.makeResourceOwnerToken();
        Profile profile = FixtureFactory.makeProfile(rot.getResourceOwner());
        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();
        RSAKeyPair keyPair = FixtureFactory.makeRSAKeyPair();

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerTokenRepository.getByAccessToken(hashedAccessToken))
                .thenReturn(rot);

        when(mockProfileRepository.getByResourceOwnerId(rot.getResourceOwner().getUuid()))
                .thenReturn(profile);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        when(mockJwtAppFactory.secureJwtBuilder(Algorithm.HS256, keyPair))
                .thenThrow(InvalidAlgorithmException.class);

        subject.build(accessToken);

    }

    @Test(expected = IdTokenException.class)
    public void buildInvalidJsonWebKeyExceptionShouldThrowIdTokenException() throws Exception{
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        ResourceOwnerToken rot = FixtureFactory.makeResourceOwnerToken();
        Profile profile = FixtureFactory.makeProfile(rot.getResourceOwner());
        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();
        RSAKeyPair keyPair = FixtureFactory.makeRSAKeyPair();

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerTokenRepository.getByAccessToken(hashedAccessToken))
                .thenReturn(rot);

        when(mockProfileRepository.getByResourceOwnerId(rot.getResourceOwner().getUuid()))
                .thenReturn(profile);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        when(mockJwtAppFactory.secureJwtBuilder(Algorithm.HS256, keyPair))
                .thenThrow(InvalidJsonWebKeyException.class);

        subject.build(accessToken);

    }

    @Test(expected = IdTokenException.class)
    public void buildMakeJwtThrowsJwtToJsonExceptionShouldThrowIdTokenException() throws Exception {
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        ResourceOwnerToken rot = FixtureFactory.makeResourceOwnerToken();
        Profile profile = FixtureFactory.makeProfile(rot.getResourceOwner());
        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();
        RSAKeyPair keyPair = FixtureFactory.makeRSAKeyPair();
        SecureJwtBuilder mockSecureJwtBuilder = mock(SecureJwtBuilder.class);
        IdToken idToken = new IdToken();

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerTokenRepository.getByAccessToken(hashedAccessToken))
                .thenReturn(rot);

        when(mockProfileRepository.getByResourceOwnerId(rot.getResourceOwner().getUuid()))
                .thenReturn(profile);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        when(mockJwtAppFactory.secureJwtBuilder(Algorithm.HS256, keyPair))
                .thenReturn(mockSecureJwtBuilder);

        when(mockIdTokenFactory.make(rot.getToken().getTokenScopes(), profile))
                .thenReturn(idToken);

        when(mockSecureJwtBuilder.build(idToken))
                .thenThrow(JwtToJsonException.class);

        subject.build(accessToken);
    }

    @Test(expected = IdTokenException.class)
    public void buildSerializeJwtThrowsJwtToJsonExceptionShouldThrowIdTokenException() throws Exception {
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        ResourceOwnerToken rot = FixtureFactory.makeResourceOwnerToken();
        Profile profile = FixtureFactory.makeProfile(rot.getResourceOwner());
        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();
        RSAKeyPair keyPair = FixtureFactory.makeRSAKeyPair();
        SecureJwtBuilder mockSecureJwtBuilder = mock(SecureJwtBuilder.class);
        IdToken idToken = new IdToken();
        JsonWebToken jsonWebToken = new JsonWebToken();
        JWTSerializer mockJwtSerializer = mock(JWTSerializer.class);

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerTokenRepository.getByAccessToken(hashedAccessToken))
                .thenReturn(rot);

        when(mockProfileRepository.getByResourceOwnerId(rot.getResourceOwner().getUuid()))
                .thenReturn(profile);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        when(mockJwtAppFactory.secureJwtBuilder(Algorithm.HS256, keyPair))
                .thenReturn(mockSecureJwtBuilder);

        when(mockIdTokenFactory.make(rot.getToken().getTokenScopes(), profile))
                .thenReturn(idToken);

        when(mockSecureJwtBuilder.build(idToken))
                .thenReturn(jsonWebToken);

        when(mockJwtAppFactory.jwtSerializer()).thenReturn(mockJwtSerializer);

        when(mockJwtSerializer.jwtToString(jsonWebToken))
                .thenThrow(JwtToJsonException.class);

        subject.build(accessToken);
    }
}