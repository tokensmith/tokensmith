package org.rootservices.authorization.openId.identity;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenClaims;
import org.rootservices.authorization.openId.identity.exception.*;
import org.rootservices.authorization.openId.identity.entity.IdToken;
import org.rootservices.authorization.openId.identity.factory.IdTokenFactory;
import org.rootservices.authorization.openId.identity.translator.PrivateKeyTranslator;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.*;
import org.rootservices.authorization.security.ciphers.HashTextStaticSalt;
import org.rootservices.jwt.config.JwtAppFactory;
import org.rootservices.jwt.entity.jwk.RSAKeyPair;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 2/19/16.
 */
public class MakeCodeGrantIdentityTokenTest {

    private MakeCodeGrantIdentityToken subject;

    @Mock
    private HashTextStaticSalt mockHashText;
    @Mock
    private ResourceOwnerRepository mockResourceOwnerRepository;
    @Mock
    private RsaPrivateKeyRepository mockRsaPrivateKeyRepository;
    @Mock
    private PrivateKeyTranslator mockPrivateKeyTranslator;
    @Mock
    private IdTokenFactory mockIdTokenFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new MakeCodeGrantIdentityToken(
                mockHashText,
                mockResourceOwnerRepository,
                mockRsaPrivateKeyRepository,
                mockPrivateKeyTranslator,
                new JwtAppFactory(),
                mockIdTokenFactory
        );
    }

    @Test
    public void makeShouldReturnEncodedJwt() throws Exception {
        TokenClaims tc = new TokenClaims();
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(ro.getId());
        ro.setProfile(profile);

        UUID clientId = UUID.randomUUID();
        Token token = FixtureFactory.makeOpenIdToken(accessToken, clientId, new ArrayList<>());
        ro.getTokens().add(token);

        RSAPrivateKey key = FixtureFactory.makeRSAPrivateKey();
        RSAKeyPair keyPair = FixtureFactory.makeRSAKeyPair();

        List<String> scopesForIdToken = ro.getTokens().get(0).getTokenScopes().stream()
                .map(item -> item.getScope().getName())
                .collect(Collectors.toList());

        IdToken idToken = new IdToken();

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerRepository.getByAccessTokenWithProfileAndTokens(hashedAccessToken))
                .thenReturn(ro);

        when(mockRsaPrivateKeyRepository.getMostRecentAndActiveForSigning())
                .thenReturn(key);

        when(mockPrivateKeyTranslator.from(key)).thenReturn(keyPair);

        when(mockIdTokenFactory.make(tc, scopesForIdToken, ro))
                .thenReturn(idToken);

        String actual = subject.make(accessToken, tc);

        assertThat(actual, is(notNullValue()));

    }

    @Test(expected = ResourceOwnerNotFoundException.class)
    public void makeShouldThrowResourceOwnerNotFoundException() throws Exception {
        TokenClaims tc = new TokenClaims();
        String accessToken = "accessToken";
        String hashedAccessToken = "hashedAccessToken";

        when(mockHashText.run(accessToken)).thenReturn(hashedAccessToken);

        when(mockResourceOwnerRepository.getByAccessTokenWithProfileAndTokens(hashedAccessToken))
                .thenThrow(RecordNotFoundException.class);

        subject.make(accessToken, tc);
    }

    @Test(expected = ProfileNotFoundException.class)
    public void makeShouldThrowProfileNotFoundException() throws Exception {
        TokenClaims tc = new TokenClaims();
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

        subject.make(accessToken, tc);
    }

    @Test(expected = KeyNotFoundException.class)
    public void makeShouldThrowKeyNotFoundException() throws Exception {
        TokenClaims tc = new TokenClaims();
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

        subject.make(accessToken, tc);
    }
}