package net.tokensmith.authorization.register;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.repository.entity.Address;
import net.tokensmith.repository.entity.FamilyName;
import net.tokensmith.repository.entity.GivenName;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.repository.exceptions.DuplicateRecordException;
import net.tokensmith.repository.repo.*;
import net.tokensmith.authorization.register.exception.RegisterException;
import net.tokensmith.authorization.register.request.UserInfo;
import net.tokensmith.authorization.register.translator.UserInfoTranslator;
import net.tokensmith.authorization.security.ciphers.HashTextRandomSalt;
import org.springframework.dao.DuplicateKeyException;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by tommackenzie on 5/16/17.
 */
public class RegisterOpenIdUserTest {
    @Mock
    private UserInfoTranslator mockUserInfoTranslator;
    @Mock
    private ResourceOwnerRepository mockResourceOwnerRepository;
    @Mock
    private ProfileRepository mockProfileRepository;
    @Mock
    private GivenNameRepository mockGivenNameRepository;
    @Mock
    private FamilyNameRepository mockFamilyNameRepository;
    @Mock
    private AddressRepository mockAddressRepository;
    @Mock
    private HashTextRandomSalt mockHashTextRandomSalt;

    private RegisterOpenIdUser subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new RegisterOpenIdUser(
                mockUserInfoTranslator,
                mockResourceOwnerRepository,
                mockProfileRepository,
                mockGivenNameRepository,
                mockFamilyNameRepository,
                mockAddressRepository,
                mockHashTextRandomSalt
        );
    }

    @Test
    public void runShouldBeOk() throws Exception {
        ResourceOwner ro = FixtureFactory.makeOpenIdResourceOwner();

        UserInfo userInfo = new UserInfo();
        userInfo.setEmail("obi-wan@tokensmith.net");
        userInfo.setPassword("password");

        String hashedPassword = "hashedPassword";
        when(mockHashTextRandomSalt.run(userInfo.getPassword())).thenReturn(hashedPassword);

        when(mockUserInfoTranslator.from(userInfo)).thenReturn(ro);

        subject.run(userInfo);


        verify(mockResourceOwnerRepository, times(1)).insert(ro);
        verify(mockProfileRepository, times(1)).insert(ro.getProfile());

        verify(mockGivenNameRepository, times(1)).insert(any(GivenName.class));
        verify(mockGivenNameRepository, times(1)).insert(ro.getProfile().getGivenNames().get(0));

        verify(mockFamilyNameRepository, times(1)).insert(any(FamilyName.class));
        verify(mockFamilyNameRepository, times(1)).insert(ro.getProfile().getFamilyNames().get(0));

        verify(mockAddressRepository, times(1)).insert(any(Address.class));
        verify(mockAddressRepository, times(1)).insert(ro.getProfile().getAddresses().get(0));
    }

    @Test
    public void runWhenEmailBlankShouldThrowRegisterException() throws Exception {
        UserInfo userInfo = new UserInfo();
        userInfo.setEmail("");

        RegisterException actual = null;
        try {
            subject.run(userInfo);
        } catch (RegisterException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(nullValue()));
        assertThat(actual.getRegisterError(), is(RegisterError.EMAIL_MISSING));
    }

    @Test
    public void runWhenEmailNullShouldThrowRegisterException() throws Exception {
        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(null);

        RegisterException actual = null;
        try {
            subject.run(userInfo);
        } catch (RegisterException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(nullValue()));
        assertThat(actual.getRegisterError(), is(RegisterError.EMAIL_MISSING));
    }

    @Test
    public void runWhenPasswordBlankShouldThrowRegisterException() throws Exception {
        UserInfo userInfo = new UserInfo();
        userInfo.setEmail("obi-wan@tokensmith.net");
        userInfo.setPassword("");

        RegisterException actual = null;
        try {
            subject.run(userInfo);
        } catch (RegisterException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(nullValue()));
        assertThat(actual.getRegisterError(), is(RegisterError.PASSWORD_MISSING));
    }

    @Test
    public void runWhenPasswordNullShouldThrowRegisterException() throws Exception {
        UserInfo userInfo = new UserInfo();
        userInfo.setEmail("obi-wan@tokensmith.net");
        userInfo.setPassword(null);

        RegisterException actual = null;
        try {
            subject.run(userInfo);
        } catch (RegisterException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(nullValue()));
        assertThat(actual.getRegisterError(), is(RegisterError.PASSWORD_MISSING));
    }

    @Test
    public void runWhenEmailAlreadyUsedShouldThrowRegisterException() throws Exception {
        ResourceOwner ro = FixtureFactory.makeOpenIdResourceOwner();

        UserInfo userInfo = new UserInfo();
        userInfo.setEmail("obi-wan@tokensmith.net");
        userInfo.setPassword("password");

        String hashedPassword = "hashedPassword";
        when(mockHashTextRandomSalt.run(userInfo.getPassword())).thenReturn(hashedPassword);

        when(mockUserInfoTranslator.from(userInfo)).thenReturn(ro);

        DuplicateKeyException dke = new DuplicateKeyException("error message");
        DuplicateRecordException dre = new DuplicateRecordException(
                "", dke, Optional.of("email")
        );
        doThrow(dre).when(mockResourceOwnerRepository).insert(any(ResourceOwner.class));

        RegisterException actual = null;
        try {
            subject.run(userInfo);
        } catch (RegisterException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(dre));
        assertThat(actual.getRegisterError(), is(RegisterError.EMAIL_TAKEN));
    }
}