package org.rootservices.authorization.register;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.nonce.entity.NonceName;
import org.rootservices.authorization.persistence.entity.Nonce;
import org.rootservices.authorization.persistence.entity.NonceType;
import org.rootservices.authorization.persistence.entity.Profile;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.NonceRepository;
import org.rootservices.authorization.persistence.repository.NonceTypeRepository;
import org.rootservices.authorization.persistence.repository.ProfileRepository;
import org.rootservices.authorization.persistence.repository.ResourceOwnerRepository;
import org.rootservices.authorization.register.exception.RegisterException;
import org.rootservices.authorization.security.RandomString;
import org.rootservices.authorization.security.ciphers.HashTextRandomSalt;
import org.rootservices.authorization.security.ciphers.HashTextStaticSalt;
import org.rootservices.pelican.Publish;
import org.springframework.dao.DuplicateKeyException;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class RegisterTest {
    private static String ISSUER = "https://sso.rootservices.org";

    @Mock
    private ResourceOwnerRepository mockResourceOwnerRepository;
    @Mock
    private ProfileRepository mockProfileRepository;
    @Mock
    private HashTextRandomSalt mockHashTextRandomSalt;
    @Mock
    private RandomString mockRandomString;
    @Mock
    private HashTextStaticSalt mockHashTextStaticSalt;
    @Mock
    private NonceTypeRepository mockNonceTypeRepository;
    @Mock
    private NonceRepository mockNonceRepository;
    @Mock
    private Publish mockPublish;

    private Register subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new Register(mockResourceOwnerRepository, mockProfileRepository, mockHashTextRandomSalt, mockRandomString, mockHashTextStaticSalt, mockNonceTypeRepository, mockNonceRepository, mockPublish, ISSUER);
    }

    @Test
    public void runShouldBeOk() throws Exception {
        String email = "obi-wan@rootservices.org";
        String password = "password";
        String repeatPassword = "password";
        String hashedPassword = "hashedPassword";
        when(mockHashTextRandomSalt.run(password)).thenReturn(hashedPassword);

        when(mockRandomString.run()).thenReturn("nonce");
        when(mockHashTextStaticSalt.run("nonce")).thenReturn("hashedNonce");

        NonceType nonceType = new NonceType(UUID.randomUUID(), "welcome", 120, OffsetDateTime.now());
        when(mockNonceTypeRepository.getByName(NonceName.WELCOME)).thenReturn(nonceType);

        ResourceOwner actual = subject.run(email, password, repeatPassword);

        ArgumentCaptor<ResourceOwner> roCaptor = ArgumentCaptor.forClass(ResourceOwner.class);
        verify(mockResourceOwnerRepository).insert(roCaptor.capture());

        assertThat(actual.getId(), is(notNullValue()));
        assertThat(actual.getEmail(), is(email));
        assertThat(actual.getPassword(), is(hashedPassword.getBytes()));

        ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);
        verify(mockProfileRepository).insert(profileCaptor.capture());

        assertThat(profileCaptor.getValue(), is(notNullValue()));
        assertThat(profileCaptor.getValue().getId(), is(notNullValue()));
        assertThat(profileCaptor.getValue().getResourceOwnerId(), is(roCaptor.getValue().getId()));
        assertThat(profileCaptor.getValue().isPhoneNumberVerified(), is(false));

        ArgumentCaptor<Nonce> nonceCaptor = ArgumentCaptor.forClass(Nonce.class);
        verify(mockNonceRepository).insert(nonceCaptor.capture());

        assertThat(nonceCaptor.getValue().getId(), is(notNullValue()));
        assertThat(nonceCaptor.getValue().getResourceOwner(), is(roCaptor.getValue()));
        assertThat(nonceCaptor.getValue().getNonceType(), is(nonceType));
        assertThat(nonceCaptor.getValue().getNonce(), is("hashedNonce".getBytes()));
        assertThat(nonceCaptor.getValue().getExpiresAt(), is(notNullValue()));

        verify(mockPublish).send(eq("mailer"), any(Map.class));
    }

    @Test
    public void runWhenEmailBlankShouldThrowRegisterException() throws Exception {
        String email = "";
        String password = "password";
        String repeatPassword = "password";

        RegisterException actual = null;
        try {
            subject.run(email, password, repeatPassword);
        } catch (RegisterException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(nullValue()));
        assertThat(actual.getRegisterError(), is(RegisterError.EMAIL_MISSING));
    }

    @Test
    public void runWhenEmailNullShouldThrowRegisterException() throws Exception {
        String email = null;
        String password = "password";
        String repeatPassword = "password";

        RegisterException actual = null;
        try {
            subject.run(email, password, repeatPassword);
        } catch (RegisterException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(nullValue()));
        assertThat(actual.getRegisterError(), is(RegisterError.EMAIL_MISSING));
    }

    @Test
    public void runWhenPasswordBlankShouldThrowRegisterException() throws Exception {
        String email = "obi-wan@rootservices.org";
        String password = "";
        String repeatPassword = "password";

        RegisterException actual = null;
        try {
            subject.run(email, password, repeatPassword);
        } catch (RegisterException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(nullValue()));
        assertThat(actual.getRegisterError(), is(RegisterError.PASSWORD_MISSING));
    }

    @Test
    public void runWhenPasswordNullShouldThrowRegisterException() throws Exception {
        String email = "obi-wan@rootservices.org";
        String password = null;
        String repeatPassword = "password";

        RegisterException actual = null;
        try {
            subject.run(email, password, repeatPassword);
        } catch (RegisterException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(nullValue()));
        assertThat(actual.getRegisterError(), is(RegisterError.PASSWORD_MISSING));
    }

    @Test
    public void runWhenRepeatPasswordBlankShouldThrowRegisterException() throws Exception {
        String email = "obi-wan@rootservices.org";
        String password = "password";
        String repeatPassword = "";

        RegisterException actual = null;
        try {
            subject.run(email, password, repeatPassword);
        } catch (RegisterException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(nullValue()));
        assertThat(actual.getRegisterError(), is(RegisterError.REPEAT_PASSWORD_MISSING));
    }

    @Test
    public void runWhenRepeatPasswordNullShouldThrowRegisterException() throws Exception {
        String email = "obi-wan@rootservices.org";
        String password = "password";
        String repeatPassword = null;

        RegisterException actual = null;
        try {
            subject.run(email, password, repeatPassword);
        } catch (RegisterException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(nullValue()));
        assertThat(actual.getRegisterError(), is(RegisterError.REPEAT_PASSWORD_MISSING));
    }

    @Test
    public void runWhenEmailAlreadyUsedShouldThrowRegisterException() throws Exception {
        String email = "obi-wan@rootservices.org";
        String password = "password";
        String repeatPassword = "password";
        String hashedPassword = "hashedPassword";
        when(mockHashTextRandomSalt.run(password)).thenReturn(hashedPassword);

        DuplicateKeyException dke = new DuplicateKeyException("error message");
        DuplicateRecordException dre = new DuplicateRecordException(
                "", dke, Optional.of("email")
        );
        doThrow(dre).when(mockResourceOwnerRepository).insert(any(ResourceOwner.class));

        RegisterException actual = null;
        try {
            subject.run(email, password, repeatPassword);
        } catch (RegisterException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(dre));
        assertThat(actual.getRegisterError(), is(RegisterError.EMAIL_TAKEN));
    }

    @Test
    public void runPasswordMismatchShouldThrowRegisterException() throws Exception {
        String email = "obi-wan@rootservices.org";
        String password = "password";
        String repeatPassword = "mismatchPassword";

        RegisterException actual = null;
        try {
            subject.run(email, password, repeatPassword);
        } catch (RegisterException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(nullValue()));
        assertThat(actual.getRegisterError(), is(RegisterError.PASSWORD_MISMATCH));
    }
}