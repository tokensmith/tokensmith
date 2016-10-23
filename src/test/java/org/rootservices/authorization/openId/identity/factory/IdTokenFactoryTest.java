package org.rootservices.authorization.openId.identity.factory;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenClaims;
import org.rootservices.authorization.openId.identity.entity.IdToken;
import org.rootservices.authorization.openId.identity.translator.AddrToAddrClaims;
import org.rootservices.authorization.openId.identity.translator.ProfileToIdToken;
import org.rootservices.authorization.persistence.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Created by tommackenzie on 3/20/16.
 */
public class IdTokenFactoryTest {

    @Mock
    private ProfileToIdToken mockProfileToIdToken;
    @Mock
    private AddrToAddrClaims mockAddrToAddrClaims;

    private IdTokenFactory subject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new IdTokenFactory(mockProfileToIdToken, mockAddrToAddrClaims);
    }

    @Test
    public void makeWhenProfileShouldOnlyAddProfileClaims() throws Exception {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(ro);

        List<String> scopes = new ArrayList<>();
        scopes.add("profile");

        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());

        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);
        IdToken actual = subject.make(tc, scopes, profile);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAddress().isPresent(), is(false));
        // TODO: 130584847 assertions

        verify(mockProfileToIdToken, times(1)).toProfileClaims(any(IdToken.class), any(Profile.class));
        verify(mockProfileToIdToken, times(0)).toEmailClaims(any(IdToken.class), any(String.class), any(Boolean.class));
        verify(mockProfileToIdToken, times(0)).toPhoneClaims(any(IdToken.class), any(Optional.class), any(Boolean.class));
        verify(mockAddrToAddrClaims, times(0)).to(any(Address.class));
    }

    @Test
    public void makeWhenEmailShouldOnlyAddEmailClaims() throws Exception {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(ro);

        List<String> scopes = new ArrayList<>();
        scopes.add("email");

        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());
        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);

        IdToken actual = subject.make(tc, scopes, profile);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAddress().isPresent(), is(false));
        // TODO: 130584847 assertions

        verify(mockProfileToIdToken, times(0)).toProfileClaims(any(IdToken.class), any(Profile.class));
        verify(mockProfileToIdToken, times(1)).toEmailClaims(any(IdToken.class), any(String.class), any(Boolean.class));
        verify(mockProfileToIdToken, times(0)).toPhoneClaims(any(IdToken.class), any(Optional.class), any(Boolean.class));
        verify(mockAddrToAddrClaims, times(0)).to(any(Address.class));
    }

    @Test
    public void makeWhenPhoneShouldOnlyAddPhoneClaims() throws Exception {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(ro);

        List<String> scopes = new ArrayList<>();
        scopes.add("phone");

        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());
        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);

        IdToken actual = subject.make(tc, scopes, profile);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAddress().isPresent(), is(false));
        // TODO: 130584847 assertions

        verify(mockProfileToIdToken, times(0)).toProfileClaims(any(IdToken.class), any(Profile.class));
        verify(mockProfileToIdToken, times(0)).toEmailClaims(any(IdToken.class), any(String.class), any(Boolean.class));
        verify(mockProfileToIdToken, times(1)).toPhoneClaims(any(IdToken.class), any(Optional.class), any(Boolean.class));
        verify(mockAddrToAddrClaims, times(0)).to(any(Address.class));
    }

    @Test
    public void makeWhenAddressShouldOnlyAddAddressClaims() throws Exception {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(ro);
        Address address = FixtureFactory.makeAddress(profile.getId());
        profile.getAddresses().add(address);

        List<String> scopes = new ArrayList<>();
        scopes.add("address");

        org.rootservices.authorization.openId.identity.entity.Address addressClaim = new org.rootservices.authorization.openId.identity.entity.Address();
        when(mockAddrToAddrClaims.to(profile.getAddresses().get(0))).thenReturn(addressClaim);

        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());
        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);

        IdToken actual = subject.make(tc, scopes, profile);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAddress().isPresent(), is(true));
        assertThat(actual.getAddress().get(), is(addressClaim));
        // TODO: 130584847 assertions

        verify(mockProfileToIdToken, times(0)).toProfileClaims(any(IdToken.class), any(Profile.class));
        verify(mockProfileToIdToken, times(0)).toEmailClaims(any(IdToken.class), any(String.class), any(Boolean.class));
        verify(mockProfileToIdToken, times(0)).toPhoneClaims(any(IdToken.class), any(Optional.class), any(Boolean.class));
        verify(mockAddrToAddrClaims, times(1)).to(any(Address.class));
    }

    @Test
    public void makeWhenAddressAndProfileHasNoAddressShouldNotAddAddressClaim() throws Exception {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(ro);

        List<String> scopes = new ArrayList<>();
        scopes.add("address");

        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());
        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);

        IdToken actual = subject.make(tc, scopes, profile);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAddress().isPresent(), is(false));

        verify(mockProfileToIdToken, times(0)).toProfileClaims(any(IdToken.class), any(Profile.class));
        verify(mockProfileToIdToken, times(0)).toEmailClaims(any(IdToken.class), any(String.class), any(Boolean.class));
        verify(mockProfileToIdToken, times(0)).toPhoneClaims(any(IdToken.class), any(Optional.class), any(Boolean.class));
        verify(mockAddrToAddrClaims, times(0)).to(any(Address.class));
    }

    @Test
    public void makeForAccessTokenAndNonceWhenProfileShouldOnlyAddProfileClaims() throws Exception{
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(ro);

        List<String> scopes = new ArrayList<>();
        scopes.add("profile");

        String accessTokenHash = "access-token-hash";
        String nonce = "some-nonce";

        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());

        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);
        IdToken actual = subject.make(accessTokenHash, nonce, tc, scopes, profile);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAccessTokenHash().isPresent(), is(true));
        assertThat(actual.getAccessTokenHash().get(), is(accessTokenHash));
        // TODO: 130584847 - add more assertions

        assertThat(actual.getNonce().isPresent(), is(true));
        assertThat(actual.getNonce().get(), is(nonce));

        assertThat(actual.getAddress().isPresent(), is(false));
        verify(mockProfileToIdToken, times(1)).toProfileClaims(any(IdToken.class), any(Profile.class));
        verify(mockProfileToIdToken, times(0)).toEmailClaims(any(IdToken.class), any(String.class), any(Boolean.class));
        verify(mockProfileToIdToken, times(0)).toPhoneClaims(any(IdToken.class), any(Optional.class), any(Boolean.class));
        verify(mockAddrToAddrClaims, times(0)).to(any(Address.class));
    }

    @Test
    public void makeForIdentityOnlyAndNonceWhenProfileShouldOnlyAddProfileClaims() throws Exception{
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(ro);

        List<String> scopes = new ArrayList<>();
        scopes.add("profile");

        String nonce = "some-nonce";
        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());
        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);

        IdToken actual = subject.make(nonce, tc, scopes, profile);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAccessTokenHash().isPresent(), is(false));

        assertThat(actual.getNonce().isPresent(), is(true));
        assertThat(actual.getNonce().get(), is(nonce));
        assertThat(actual.getAddress().isPresent(), is(false));
        // TODO: 130584847 - add more assertions

        verify(mockProfileToIdToken, times(1)).toProfileClaims(any(IdToken.class), any(Profile.class));
        verify(mockProfileToIdToken, times(0)).toEmailClaims(any(IdToken.class), any(String.class), any(Boolean.class));
        verify(mockProfileToIdToken, times(0)).toPhoneClaims(any(IdToken.class), any(Optional.class), any(Boolean.class));
        verify(mockAddrToAddrClaims, times(0)).to(any(Address.class));
    }
}