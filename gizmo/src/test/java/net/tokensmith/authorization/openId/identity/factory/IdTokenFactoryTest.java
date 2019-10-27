package net.tokensmith.authorization.openId.identity.factory;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenClaims;
import net.tokensmith.authorization.openId.identity.entity.IdToken;
import net.tokensmith.authorization.openId.identity.translator.AddrToAddrClaims;
import net.tokensmith.authorization.openId.identity.translator.ProfileToIdToken;
import net.tokensmith.authorization.persistence.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
        Profile profile = FixtureFactory.makeProfile(ro.getId());
        ro.setProfile(profile);

        List<String> scopes = new ArrayList<>();
        scopes.add("profile");

        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());

        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);
        IdToken actual = subject.make(tc, scopes, ro);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAddress().isPresent(), is(false));

        assertThat(actual.getIssuer().isPresent(), is(true));
        assertThat(actual.getIssuer().get(), is(tc.getIssuer()));
        assertThat(actual.getAudience(), is(notNullValue()));
        assertThat(actual.getAudience().size(), is(1));
        assertThat(actual.getAudience().get(0), is(tc.getAudience().get(0)));
        assertThat(actual.getIssuedAt().isPresent(), is(true));
        assertThat(actual.getIssuedAt().get(), is(tc.getIssuedAt()));
        assertThat(actual.getExpirationTime().isPresent(), is(true));
        assertThat(actual.getExpirationTime().get(), is(tc.getExpirationTime()));
        assertThat(actual.getAuthenticationTime(), is(notNullValue()));
        assertThat(actual.getAuthenticationTime(), is(tc.getAuthTime()));

        verify(mockProfileToIdToken, times(1)).toProfileClaims(any(IdToken.class), any(Profile.class));
        verify(mockProfileToIdToken, times(0)).toEmailClaims(any(IdToken.class), any(String.class), any(Boolean.class));
        verify(mockProfileToIdToken, times(0)).toPhoneClaims(any(IdToken.class), FixtureFactory.anyOptionalString(), any(Boolean.class));
        verify(mockAddrToAddrClaims, times(0)).to(any(Address.class));
    }

    @Test
    public void makeWhenHasProfileScopeAndProfileIsNullShouldNotAddProfileClaims() throws Exception {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();

        List<String> scopes = new ArrayList<>();
        scopes.add("profile");

        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());

        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);
        IdToken actual = subject.make(tc, scopes, ro);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAddress().isPresent(), is(false));

        assertThat(actual.getIssuer().isPresent(), is(true));
        assertThat(actual.getIssuer().get(), is(tc.getIssuer()));
        assertThat(actual.getAudience(), is(notNullValue()));
        assertThat(actual.getAudience().size(), is(1));
        assertThat(actual.getAudience().get(0), is(tc.getAudience().get(0)));
        assertThat(actual.getIssuedAt().isPresent(), is(true));
        assertThat(actual.getIssuedAt().get(), is(tc.getIssuedAt()));
        assertThat(actual.getExpirationTime().isPresent(), is(true));
        assertThat(actual.getExpirationTime().get(), is(tc.getExpirationTime()));
        assertThat(actual.getAuthenticationTime(), is(notNullValue()));
        assertThat(actual.getAuthenticationTime(), is(tc.getAuthTime()));

        verify(mockProfileToIdToken, times(0)).toProfileClaims(any(IdToken.class), any(Profile.class));
        verify(mockProfileToIdToken, times(0)).toEmailClaims(any(IdToken.class), any(String.class), any(Boolean.class));

        verify(mockProfileToIdToken, times(0)).toPhoneClaims(any(IdToken.class), FixtureFactory.anyOptionalString(), any(Boolean.class));
        verify(mockAddrToAddrClaims, times(0)).to(any(Address.class));
    }

    @Test
    public void makeWhenEmailShouldOnlyAddEmailClaims() throws Exception {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(ro.getId());
        ro.setProfile(profile);

        List<String> scopes = new ArrayList<>();
        scopes.add("email");

        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());
        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);

        IdToken actual = subject.make(tc, scopes, ro);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAddress().isPresent(), is(false));

        assertThat(actual.getIssuer().isPresent(), is(true));
        assertThat(actual.getIssuer().get(), is(tc.getIssuer()));
        assertThat(actual.getAudience(), is(notNullValue()));
        assertThat(actual.getAudience().size(), is(1));
        assertThat(actual.getAudience().get(0), is(tc.getAudience().get(0)));
        assertThat(actual.getIssuedAt().isPresent(), is(true));
        assertThat(actual.getIssuedAt().get(), is(tc.getIssuedAt()));
        assertThat(actual.getExpirationTime().isPresent(), is(true));
        assertThat(actual.getExpirationTime().get(), is(tc.getExpirationTime()));
        assertThat(actual.getAuthenticationTime(), is(notNullValue()));
        assertThat(actual.getAuthenticationTime(), is(tc.getAuthTime()));

        verify(mockProfileToIdToken, times(0)).toProfileClaims(any(IdToken.class), any(Profile.class));
        verify(mockProfileToIdToken, times(1)).toEmailClaims(any(IdToken.class), any(String.class), any(Boolean.class));
        verify(mockProfileToIdToken, times(0)).toPhoneClaims(any(IdToken.class), FixtureFactory.anyOptionalString(), any(Boolean.class));
        verify(mockAddrToAddrClaims, times(0)).to(any(Address.class));
    }

    @Test
    public void makeWhenPhoneShouldOnlyAddPhoneClaims() throws Exception {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(ro.getId());
        ro.setProfile(profile);

        List<String> scopes = new ArrayList<>();
        scopes.add("phone");

        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());
        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);

        IdToken actual = subject.make(tc, scopes, ro);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAddress().isPresent(), is(false));

        assertThat(actual.getIssuer().isPresent(), is(true));
        assertThat(actual.getIssuer().get(), is(tc.getIssuer()));
        assertThat(actual.getAudience(), is(notNullValue()));
        assertThat(actual.getAudience().size(), is(1));
        assertThat(actual.getAudience().get(0), is(tc.getAudience().get(0)));
        assertThat(actual.getIssuedAt().isPresent(), is(true));
        assertThat(actual.getIssuedAt().get(), is(tc.getIssuedAt()));
        assertThat(actual.getExpirationTime().isPresent(), is(true));
        assertThat(actual.getExpirationTime().get(), is(tc.getExpirationTime()));
        assertThat(actual.getAuthenticationTime(), is(notNullValue()));
        assertThat(actual.getAuthenticationTime(), is(tc.getAuthTime()));

        verify(mockProfileToIdToken, times(0)).toProfileClaims(any(IdToken.class), any(Profile.class));
        verify(mockProfileToIdToken, times(0)).toEmailClaims(any(IdToken.class), any(String.class), any(Boolean.class));
        verify(mockProfileToIdToken, times(1)).toPhoneClaims(any(IdToken.class), FixtureFactory.anyOptionalString(), any(Boolean.class));
        verify(mockAddrToAddrClaims, times(0)).to(any(Address.class));
    }

    @Test
    public void makeWhenHasPhoneScopeAndProfileIsNullShouldNotAddPhoneClaims() throws Exception {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();

        List<String> scopes = new ArrayList<>();
        scopes.add("phone");

        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());
        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);

        IdToken actual = subject.make(tc, scopes, ro);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAddress().isPresent(), is(false));

        assertThat(actual.getIssuer().isPresent(), is(true));
        assertThat(actual.getIssuer().get(), is(tc.getIssuer()));
        assertThat(actual.getAudience(), is(notNullValue()));
        assertThat(actual.getAudience().size(), is(1));
        assertThat(actual.getAudience().get(0), is(tc.getAudience().get(0)));
        assertThat(actual.getIssuedAt().isPresent(), is(true));
        assertThat(actual.getIssuedAt().get(), is(tc.getIssuedAt()));
        assertThat(actual.getExpirationTime().isPresent(), is(true));
        assertThat(actual.getExpirationTime().get(), is(tc.getExpirationTime()));
        assertThat(actual.getAuthenticationTime(), is(notNullValue()));
        assertThat(actual.getAuthenticationTime(), is(tc.getAuthTime()));

        verify(mockProfileToIdToken, times(0)).toProfileClaims(any(IdToken.class), any(Profile.class));
        verify(mockProfileToIdToken, times(0)).toEmailClaims(any(IdToken.class), any(String.class), any(Boolean.class));
        verify(mockProfileToIdToken, times(0)).toPhoneClaims(any(IdToken.class), FixtureFactory.anyOptionalString(), any(Boolean.class));
        verify(mockAddrToAddrClaims, times(0)).to(any(Address.class));
    }

    @Test
    public void makeWhenAddressShouldOnlyAddAddressClaims() throws Exception {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(ro.getId());
        Address address = FixtureFactory.makeAddress(profile.getId());
        profile.getAddresses().add(address);
        ro.setProfile(profile);

        List<String> scopes = new ArrayList<>();
        scopes.add("address");

        net.tokensmith.authorization.openId.identity.entity.Address addressClaim = new net.tokensmith.authorization.openId.identity.entity.Address();
        when(mockAddrToAddrClaims.to(profile.getAddresses().get(0))).thenReturn(addressClaim);

        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());
        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);

        IdToken actual = subject.make(tc, scopes, ro);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAddress().isPresent(), is(true));
        assertThat(actual.getAddress().get(), is(addressClaim));

        assertThat(actual.getIssuer().isPresent(), is(true));
        assertThat(actual.getIssuer().get(), is(tc.getIssuer()));
        assertThat(actual.getAudience(), is(notNullValue()));
        assertThat(actual.getAudience().size(), is(1));
        assertThat(actual.getAudience().get(0), is(tc.getAudience().get(0)));
        assertThat(actual.getIssuedAt().isPresent(), is(true));
        assertThat(actual.getIssuedAt().get(), is(tc.getIssuedAt()));
        assertThat(actual.getExpirationTime().isPresent(), is(true));
        assertThat(actual.getExpirationTime().get(), is(tc.getExpirationTime()));
        assertThat(actual.getAuthenticationTime(), is(notNullValue()));
        assertThat(actual.getAuthenticationTime(), is(tc.getAuthTime()));

        verify(mockProfileToIdToken, times(0)).toProfileClaims(any(IdToken.class), any(Profile.class));
        verify(mockProfileToIdToken, times(0)).toEmailClaims(any(IdToken.class), any(String.class), any(Boolean.class));
        verify(mockProfileToIdToken, times(0)).toPhoneClaims(any(IdToken.class), FixtureFactory.anyOptionalString(), any(Boolean.class));
        verify(mockAddrToAddrClaims, times(1)).to(any(Address.class));
    }

    @Test
    public void makeWhenHasAddressScopeAndProfileIsNullShouldNotAddAddAddressClaims() throws Exception {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();

        List<String> scopes = new ArrayList<>();
        scopes.add("address");

        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());
        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);

        IdToken actual = subject.make(tc, scopes, ro);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAddress().isPresent(), is(false));
        assertThat(actual.getIssuer().isPresent(), is(true));
        assertThat(actual.getIssuer().get(), is(tc.getIssuer()));
        assertThat(actual.getAudience(), is(notNullValue()));
        assertThat(actual.getAudience().size(), is(1));
        assertThat(actual.getAudience().get(0), is(tc.getAudience().get(0)));
        assertThat(actual.getIssuedAt().isPresent(), is(true));
        assertThat(actual.getIssuedAt().get(), is(tc.getIssuedAt()));
        assertThat(actual.getExpirationTime().isPresent(), is(true));
        assertThat(actual.getExpirationTime().get(), is(tc.getExpirationTime()));
        assertThat(actual.getAuthenticationTime(), is(notNullValue()));
        assertThat(actual.getAuthenticationTime(), is(tc.getAuthTime()));

        verify(mockProfileToIdToken, times(0)).toProfileClaims(any(IdToken.class), any(Profile.class));
        verify(mockProfileToIdToken, times(0)).toEmailClaims(any(IdToken.class), any(String.class), any(Boolean.class));
        verify(mockProfileToIdToken, times(0)).toPhoneClaims(any(IdToken.class), FixtureFactory.anyOptionalString(), any(Boolean.class));
        verify(mockAddrToAddrClaims, times(0)).to(any(Address.class));
    }

    @Test
    public void makeWhenAddressAndProfileHasNoAddressShouldNotAddAddressClaim() throws Exception {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(ro.getId());
        ro.setProfile(profile);

        List<String> scopes = new ArrayList<>();
        scopes.add("address");

        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());
        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);

        IdToken actual = subject.make(tc, scopes, ro);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAddress().isPresent(), is(false));

        assertThat(actual.getIssuer().isPresent(), is(true));
        assertThat(actual.getIssuer().get(), is(tc.getIssuer()));
        assertThat(actual.getAudience(), is(notNullValue()));
        assertThat(actual.getAudience().size(), is(1));
        assertThat(actual.getAudience().get(0), is(tc.getAudience().get(0)));
        assertThat(actual.getIssuedAt().isPresent(), is(true));
        assertThat(actual.getIssuedAt().get(), is(tc.getIssuedAt()));
        assertThat(actual.getExpirationTime().isPresent(), is(true));
        assertThat(actual.getExpirationTime().get(), is(tc.getExpirationTime()));
        assertThat(actual.getAuthenticationTime(), is(notNullValue()));
        assertThat(actual.getAuthenticationTime(), is(tc.getAuthTime()));

        verify(mockProfileToIdToken, times(0)).toProfileClaims(any(IdToken.class), any(Profile.class));
        verify(mockProfileToIdToken, times(0)).toEmailClaims(any(IdToken.class), any(String.class), any(Boolean.class));
        verify(mockProfileToIdToken, times(0)).toPhoneClaims(any(IdToken.class), FixtureFactory.anyOptionalString(), any(Boolean.class));
        verify(mockAddrToAddrClaims, times(0)).to(any(Address.class));
    }

    @Test
    public void makeForAccessTokenAndNonceWhenProfileShouldOnlyAddProfileClaims() throws Exception{
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(ro.getId());
        ro.setProfile(profile);

        List<String> scopes = new ArrayList<>();
        scopes.add("profile");

        String accessTokenHash = "access-token-hash";
        String nonce = "some-nonce";

        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());

        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);
        IdToken actual = subject.make(accessTokenHash, nonce, tc, scopes, ro);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAccessTokenHash().isPresent(), is(true));
        assertThat(actual.getAccessTokenHash().get(), is(accessTokenHash));

        assertThat(actual.getIssuer().isPresent(), is(true));
        assertThat(actual.getIssuer().get(), is(tc.getIssuer()));
        assertThat(actual.getAudience(), is(notNullValue()));
        assertThat(actual.getAudience().size(), is(1));
        assertThat(actual.getAudience().get(0), is(tc.getAudience().get(0)));
        assertThat(actual.getIssuedAt().isPresent(), is(true));
        assertThat(actual.getIssuedAt().get(), is(tc.getIssuedAt()));
        assertThat(actual.getExpirationTime().isPresent(), is(true));
        assertThat(actual.getExpirationTime().get(), is(tc.getExpirationTime()));
        assertThat(actual.getAuthenticationTime(), is(notNullValue()));
        assertThat(actual.getAuthenticationTime(), is(tc.getAuthTime()));

        assertThat(actual.getNonce().isPresent(), is(true));
        assertThat(actual.getNonce().get(), is(nonce));

        assertThat(actual.getAddress().isPresent(), is(false));
        verify(mockProfileToIdToken, times(1)).toProfileClaims(any(IdToken.class), any(Profile.class));
        verify(mockProfileToIdToken, times(0)).toEmailClaims(any(IdToken.class), any(String.class), any(Boolean.class));
        verify(mockProfileToIdToken, times(0)).toPhoneClaims(any(IdToken.class), FixtureFactory.anyOptionalString(), any(Boolean.class));
        verify(mockAddrToAddrClaims, times(0)).to(any(Address.class));
    }

    @Test
    public void makeForIdentityOnlyAndNonceWhenProfileShouldOnlyAddProfileClaims() throws Exception{
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(ro.getId());
        ro.setProfile(profile);

        List<String> scopes = new ArrayList<>();
        scopes.add("profile");

        String nonce = "some-nonce";
        List<String> audience = new ArrayList<>();
        audience.add(UUID.randomUUID().toString());
        TokenClaims tc = FixtureFactory.makeTokenClaims(audience);

        IdToken actual = subject.make(nonce, tc, scopes, ro);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAccessTokenHash().isPresent(), is(false));

        assertThat(actual.getNonce().isPresent(), is(true));
        assertThat(actual.getNonce().get(), is(nonce));
        assertThat(actual.getAddress().isPresent(), is(false));

        assertThat(actual.getIssuer().isPresent(), is(true));
        assertThat(actual.getIssuer().get(), is(tc.getIssuer()));
        assertThat(actual.getAudience(), is(notNullValue()));
        assertThat(actual.getAudience().size(), is(1));
        assertThat(actual.getAudience().get(0), is(tc.getAudience().get(0)));
        assertThat(actual.getIssuedAt().isPresent(), is(true));
        assertThat(actual.getIssuedAt().get(), is(tc.getIssuedAt()));
        assertThat(actual.getExpirationTime().isPresent(), is(true));
        assertThat(actual.getExpirationTime().get(), is(tc.getExpirationTime()));
        assertThat(actual.getAuthenticationTime(), is(notNullValue()));
        assertThat(actual.getAuthenticationTime(), is(tc.getAuthTime()));

        verify(mockProfileToIdToken, times(1)).toProfileClaims(any(IdToken.class), any(Profile.class));
        verify(mockProfileToIdToken, times(0)).toEmailClaims(any(IdToken.class), any(String.class), any(Boolean.class));
        verify(mockProfileToIdToken, times(0)).toPhoneClaims(any(IdToken.class), FixtureFactory.anyOptionalString(), any(Boolean.class));

        verify(mockAddrToAddrClaims, times(0)).to(any(Address.class));
    }
}