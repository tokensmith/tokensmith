package org.rootservices.authorization.grant.openid.protocol.token.factory;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.grant.openid.protocol.token.response.entity.IdToken;
import org.rootservices.authorization.grant.openid.protocol.token.translator.AddrToAddrClaims;
import org.rootservices.authorization.grant.openid.protocol.token.translator.ProfileToIdToken;
import org.rootservices.authorization.persistence.entity.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
public class IdTokenFactoryImplTest {

    @Mock
    private ProfileToIdToken mockProfileToIdToken;
    @Mock
    private AddrToAddrClaims mockAddrToAddrClaims;

    private IdTokenFactory subject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new IdTokenFactoryImpl(mockProfileToIdToken, mockAddrToAddrClaims);
    }

    @Test
    public void makeWhenProfileShouldOnlyAddProfileClaims() throws Exception {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(ro);

        List<AccessRequestScope> accessRequestScopes = new ArrayList<>();
        AccessRequestScope ars = new AccessRequestScope();
        Scope scope = new Scope();
        scope.setName("profile");
        ars.setScope(scope);
        accessRequestScopes.add(ars);


        IdToken actual = subject.make(accessRequestScopes, profile);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAddress().isPresent(), is(false));
        verify(mockProfileToIdToken, times(1)).toProfileClaims(any(IdToken.class), any(Profile.class));
        verify(mockProfileToIdToken, times(0)).toEmailClaims(any(IdToken.class), any(String.class), any(Boolean.class));
        verify(mockProfileToIdToken, times(0)).toPhoneClaims(any(IdToken.class), any(Optional.class), any(Boolean.class));
        verify(mockAddrToAddrClaims, times(0)).to(any(Address.class));
    }

    @Test
    public void makeWhenEmailShouldOnlyAddEmailClaims() throws Exception {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(ro);

        List<AccessRequestScope> accessRequestScopes = new ArrayList<>();
        AccessRequestScope ars = new AccessRequestScope();
        Scope scope = new Scope();
        scope.setName("email");
        ars.setScope(scope);
        accessRequestScopes.add(ars);

        IdToken actual = subject.make(accessRequestScopes, profile);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAddress().isPresent(), is(false));
        verify(mockProfileToIdToken, times(0)).toProfileClaims(any(IdToken.class), any(Profile.class));
        verify(mockProfileToIdToken, times(1)).toEmailClaims(any(IdToken.class), any(String.class), any(Boolean.class));
        verify(mockProfileToIdToken, times(0)).toPhoneClaims(any(IdToken.class), any(Optional.class), any(Boolean.class));
        verify(mockAddrToAddrClaims, times(0)).to(any(Address.class));
    }

    @Test
    public void makeWhenPhoneShouldOnlyAddhoneClaims() throws Exception {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(ro);

        List<AccessRequestScope> accessRequestScopes = new ArrayList<>();
        AccessRequestScope ars = new AccessRequestScope();
        Scope scope = new Scope();
        scope.setName("phone");
        ars.setScope(scope);
        accessRequestScopes.add(ars);

        IdToken actual = subject.make(accessRequestScopes, profile);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAddress().isPresent(), is(false));
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

        List<AccessRequestScope> accessRequestScopes = new ArrayList<>();
        AccessRequestScope ars = new AccessRequestScope();
        Scope scope = new Scope();
        scope.setName("address");
        ars.setScope(scope);
        accessRequestScopes.add(ars);

        org.rootservices.authorization.grant.openid.protocol.token.response.entity.Address addressClaim = new org.rootservices.authorization.grant.openid.protocol.token.response.entity.Address();
        when(mockAddrToAddrClaims.to(profile.getAddresses().get(0))).thenReturn(addressClaim);

        IdToken actual = subject.make(accessRequestScopes, profile);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAddress().isPresent(), is(true));
        assertThat(actual.getAddress().get(), is(addressClaim));

        verify(mockProfileToIdToken, times(0)).toProfileClaims(any(IdToken.class), any(Profile.class));
        verify(mockProfileToIdToken, times(0)).toEmailClaims(any(IdToken.class), any(String.class), any(Boolean.class));
        verify(mockProfileToIdToken, times(0)).toPhoneClaims(any(IdToken.class), any(Optional.class), any(Boolean.class));
        verify(mockAddrToAddrClaims, times(1)).to(any(Address.class));
    }

    @Test
    public void makeWhenAddressAndProfileHasNoAddressShouldNotAddAddressClaim() throws Exception {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        Profile profile = FixtureFactory.makeProfile(ro);

        List<AccessRequestScope> accessRequestScopes = new ArrayList<>();
        AccessRequestScope ars = new AccessRequestScope();
        Scope scope = new Scope();
        scope.setName("address");
        ars.setScope(scope);
        accessRequestScopes.add(ars);

        IdToken actual = subject.make(accessRequestScopes, profile);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAddress().isPresent(), is(false));

        verify(mockProfileToIdToken, times(0)).toProfileClaims(any(IdToken.class), any(Profile.class));
        verify(mockProfileToIdToken, times(0)).toEmailClaims(any(IdToken.class), any(String.class), any(Boolean.class));
        verify(mockProfileToIdToken, times(0)).toPhoneClaims(any(IdToken.class), any(Optional.class), any(Boolean.class));
        verify(mockAddrToAddrClaims, times(0)).to(any(Address.class));
    }
}