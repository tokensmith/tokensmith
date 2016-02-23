package org.rootservices.authorization.grant.openid.protocol.token;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.grant.openid.protocol.token.exception.IdTokenException;
import org.rootservices.authorization.grant.openid.protocol.token.exception.KeyNotFoundException;
import org.rootservices.authorization.grant.openid.protocol.token.exception.ResourceOwnerNotFoundException;
import org.rootservices.authorization.grant.openid.protocol.token.response.entity.IdToken;
import org.rootservices.authorization.grant.openid.protocol.token.translator.PrivateKeyTranslator;
import org.rootservices.authorization.persistence.entity.RSAPrivateKey;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ResourceOwnerRepository;
import org.rootservices.authorization.persistence.repository.RsaPrivateKeyRepository;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 2/19/16.
 */
public class BuildIdentityTokenImplTest {
    private BuildIdentityToken subject;

    @Mock
    private HashTextStaticSalt mockHashText;
    @Mock
    private RsaPrivateKeyRepository mockRsaPrivateKeyRepository;
    @Mock
    private ResourceOwnerRepository mockResourceOwnerRepository;
    @Mock
    private PrivateKeyTranslator mockPrivateKeyTranslator;
    @Mock
    private AppFactory mockJwtAppFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new BuildIdentityTokenImpl(
                mockHashText,
                mockRsaPrivateKeyRepository,
                mockResourceOwnerRepository,
                mockPrivateKeyTranslator,
                mockJwtAppFactory
        );
    }

    @Test
    public void buildShouldMakeJwt() throws RecordNotFoundException, InvalidAlgorithmException, InvalidJsonWebKeyException, JwtToJsonException, KeyNotFoundException, ResourceOwnerNotFoundException, IdTokenException {
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();
        RSAKeyPair keyPair = FixtureFactory.makeRSAKeyPair();
        SecureJwtBuilder mockSecureJwtBuilder = mock(SecureJwtBuilder.class);
        JsonWebToken jsonWebToken = new JsonWebToken();
        JWTSerializer mockJwtSerializer = mock(JWTSerializer.class);
        String expected = "some-compact-jwt";

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerRepository.getByAccessToken(hashedAccessToken.getBytes()))
                .thenReturn(resourceOwner);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        when(mockJwtAppFactory.secureJwtBuilder(Algorithm.HS256, keyPair))
                .thenReturn(mockSecureJwtBuilder);

        when(mockSecureJwtBuilder.build(any(IdToken.class)))
                .thenReturn(jsonWebToken);

        when(mockJwtAppFactory.jwtSerializer()).thenReturn(mockJwtSerializer);

        when(mockJwtSerializer.jwtToString(jsonWebToken))
                .thenReturn(expected);

        String actual = subject.build(accessToken);

        assertThat(actual, is(expected));

    }

    @Test(expected = ResourceOwnerNotFoundException.class)
    public void buildShouldThrowResourceOwnerNotFoundException() throws RecordNotFoundException, KeyNotFoundException, ResourceOwnerNotFoundException, IdTokenException {
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerRepository.getByAccessToken(hashedAccessToken.getBytes()))
                .thenThrow(RecordNotFoundException.class);

        subject.build(accessToken);
    }

    @Test(expected = KeyNotFoundException.class)
    public void buildShouldThrowKeyNotFoundException() throws RecordNotFoundException, KeyNotFoundException, ResourceOwnerNotFoundException, IdTokenException {
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerRepository.getByAccessToken(hashedAccessToken.getBytes()))
                .thenReturn(resourceOwner);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenThrow(KeyNotFoundException.class);

        subject.build(accessToken);

    }

    @Test(expected = IdTokenException.class)
    public void buildInvalidAlgorithmExceptionShouldThrowIdTokenException() throws RecordNotFoundException, InvalidAlgorithmException, InvalidJsonWebKeyException, KeyNotFoundException, ResourceOwnerNotFoundException, IdTokenException {
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();
        RSAKeyPair keyPair = FixtureFactory.makeRSAKeyPair();

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerRepository.getByAccessToken(hashedAccessToken.getBytes()))
                .thenReturn(resourceOwner);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        when(mockJwtAppFactory.secureJwtBuilder(Algorithm.HS256, keyPair))
                .thenThrow(InvalidAlgorithmException.class);

        subject.build(accessToken);

    }

    @Test(expected = IdTokenException.class)
    public void buildInvalidJsonWebKeyExceptionShouldThrowIdTokenException() throws RecordNotFoundException, IdTokenException, KeyNotFoundException, InvalidJsonWebKeyException, InvalidAlgorithmException, ResourceOwnerNotFoundException {
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();
        RSAKeyPair keyPair = FixtureFactory.makeRSAKeyPair();

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerRepository.getByAccessToken(hashedAccessToken.getBytes()))
                .thenReturn(resourceOwner);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        when(mockJwtAppFactory.secureJwtBuilder(Algorithm.HS256, keyPair))
                .thenThrow(InvalidJsonWebKeyException.class);

        subject.build(accessToken);

    }

    @Test(expected = IdTokenException.class)
    public void buildMakeJwtThrowsJwtToJsonExceptionShouldThrowIdTokenException() throws RecordNotFoundException, InvalidAlgorithmException, InvalidJsonWebKeyException, KeyNotFoundException, ResourceOwnerNotFoundException, IdTokenException, JwtToJsonException {
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();
        RSAKeyPair keyPair = FixtureFactory.makeRSAKeyPair();
        SecureJwtBuilder mockSecureJwtBuilder = mock(SecureJwtBuilder.class);

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerRepository.getByAccessToken(hashedAccessToken.getBytes()))
                .thenReturn(resourceOwner);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        when(mockJwtAppFactory.secureJwtBuilder(Algorithm.HS256, keyPair))
                .thenReturn(mockSecureJwtBuilder);

        when(mockSecureJwtBuilder.build(any(IdToken.class)))
                .thenThrow(JwtToJsonException.class);

        subject.build(accessToken);
    }

    @Test(expected = IdTokenException.class)
    public void buildSerializeJwtThrowsJwtToJsonExceptionShouldThrowIdTokenException() throws RecordNotFoundException, InvalidAlgorithmException, InvalidJsonWebKeyException, JwtToJsonException, KeyNotFoundException, ResourceOwnerNotFoundException, IdTokenException {
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();
        RSAKeyPair keyPair = FixtureFactory.makeRSAKeyPair();
        SecureJwtBuilder mockSecureJwtBuilder = mock(SecureJwtBuilder.class);
        JsonWebToken jsonWebToken = new JsonWebToken();
        JWTSerializer mockJwtSerializer = mock(JWTSerializer.class);

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerRepository.getByAccessToken(hashedAccessToken.getBytes()))
                .thenReturn(resourceOwner);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        when(mockJwtAppFactory.secureJwtBuilder(Algorithm.HS256, keyPair))
                .thenReturn(mockSecureJwtBuilder);

        when(mockSecureJwtBuilder.build(any(IdToken.class)))
                .thenReturn(jsonWebToken);

        when(mockJwtAppFactory.jwtSerializer()).thenReturn(mockJwtSerializer);

        when(mockJwtSerializer.jwtToString(jsonWebToken))
                .thenThrow(JwtToJsonException.class);

        subject.build(accessToken);
    }
}