package net.tokensmith.authorization.register;

import net.tokensmith.authorization.register.exception.RegisterException;
import net.tokensmith.authorization.security.RandomString;
import net.tokensmith.authorization.security.ciphers.HashTextRandomSalt;
import net.tokensmith.authorization.security.ciphers.HashToken;
import net.tokensmith.pelican.Publish;
import net.tokensmith.repository.entity.Nonce;
import net.tokensmith.repository.entity.NonceName;
import net.tokensmith.repository.entity.NonceType;
import net.tokensmith.repository.entity.Profile;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.repository.exceptions.DuplicateRecordException;
import net.tokensmith.repository.repo.NonceRepository;
import net.tokensmith.repository.repo.NonceTypeRepository;
import net.tokensmith.repository.repo.ProfileRepository;
import net.tokensmith.repository.repo.ResourceOwnerRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DuplicateKeyException;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class RegisterTest {
    private static String BASE_URI = "https://sso.tokensmith.net";

    @Mock
    private ResourceOwnerRepository mockResourceOwnerRepository;
    @Mock
    private ProfileRepository mockProfileRepository;
    @Mock
    private HashTextRandomSalt mockHashTextRandomSalt;
    @Mock
    private RandomString mockRandomString;
    @Mock
    private HashToken mockHashToken;
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
        subject = new Register(mockResourceOwnerRepository, mockProfileRepository, mockHashTextRandomSalt, mockRandomString, mockHashToken, mockNonceTypeRepository, mockNonceRepository, mockPublish);
    }

    @Test
    public void runShouldBeOk() throws Exception {
        String email = "obi-wan@tokensmith.net";
        String password = "password";
        String repeatPassword = "password";
        String hashedPassword = "hashedPassword";

        when(mockHashTextRandomSalt.run(password)).thenReturn(hashedPassword);

        when(mockRandomString.run()).thenReturn("nonce");
        when(mockHashToken.run("nonce")).thenReturn("hashedNonce");

        NonceType nonceType = new NonceType(UUID.randomUUID(), "welcome", 120, OffsetDateTime.now());
        when(mockNonceTypeRepository.getByName(NonceName.WELCOME)).thenReturn(nonceType);

        ResourceOwner actual = subject.run(email, password, repeatPassword, BASE_URI);

        ArgumentCaptor<ResourceOwner> roCaptor = ArgumentCaptor.forClass(ResourceOwner.class);
        verify(mockResourceOwnerRepository).insert(roCaptor.capture());

        assertThat(actual.getId(), is(notNullValue()));
        assertThat(actual.getEmail(), is(email));
        assertThat(actual.getPassword(), is(hashedPassword));

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
        assertThat(nonceCaptor.getValue().getNonce(), is("hashedNonce"));
        assertThat(nonceCaptor.getValue().getExpiresAt(), is(notNullValue()));

        verify(mockPublish).send(eq("message-user"), anyMap());
    }

    @Test
    public void runWhenEmailBlankShouldThrowRegisterException() throws Exception {
        String email = "";
        String password = "password";
        String repeatPassword = "password";

        RegisterException actual = null;
        try {
            subject.run(email, password, repeatPassword, BASE_URI);
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
            subject.run(email, password, repeatPassword, BASE_URI);
        } catch (RegisterException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(nullValue()));
        assertThat(actual.getRegisterError(), is(RegisterError.EMAIL_MISSING));
    }

    @Test
    public void runWhenPasswordBlankShouldThrowRegisterException() throws Exception {
        String email = "obi-wan@tokensmith.net";
        String password = "";
        String repeatPassword = "password";

        RegisterException actual = null;
        try {
            subject.run(email, password, repeatPassword, BASE_URI);
        } catch (RegisterException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(nullValue()));
        assertThat(actual.getRegisterError(), is(RegisterError.PASSWORD_MISSING));
    }

    @Test
    public void runWhenPasswordNullShouldThrowRegisterException() throws Exception {
        String email = "obi-wan@tokensmith.net";
        String password = null;
        String repeatPassword = "password";

        RegisterException actual = null;
        try {
            subject.run(email, password, repeatPassword, BASE_URI);
        } catch (RegisterException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(nullValue()));
        assertThat(actual.getRegisterError(), is(RegisterError.PASSWORD_MISSING));
    }

    @Test
    public void runWhenRepeatPasswordBlankShouldThrowRegisterException() throws Exception {
        String email = "obi-wan@tokensmith.net";
        String password = "password";
        String repeatPassword = "";

        RegisterException actual = null;
        try {
            subject.run(email, password, repeatPassword, BASE_URI);
        } catch (RegisterException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(nullValue()));
        assertThat(actual.getRegisterError(), is(RegisterError.REPEAT_PASSWORD_MISSING));
    }

    @Test
    public void runWhenRepeatPasswordNullShouldThrowRegisterException() throws Exception {
        String email = "obi-wan@tokensmith.net";
        String password = "password";
        String repeatPassword = null;

        RegisterException actual = null;
        try {
            subject.run(email, password, repeatPassword, BASE_URI);
        } catch (RegisterException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(nullValue()));
        assertThat(actual.getRegisterError(), is(RegisterError.REPEAT_PASSWORD_MISSING));
    }

    @Test
    public void runWhenEmailAlreadyUsedShouldThrowRegisterException() throws Exception {
        String email = "obi-wan@tokensmith.net";
        String password = "password";
        String repeatPassword = "password";
        String hashedPassword = "hashedPassword";
        String baseURI = "https://tokensmith.net";
        when(mockHashTextRandomSalt.run(password)).thenReturn(hashedPassword);

        DuplicateKeyException dke = new DuplicateKeyException("error message");
        DuplicateRecordException dre = new DuplicateRecordException(
                "", dke, Optional.of("email")
        );
        doThrow(dre).when(mockResourceOwnerRepository).insert(any(ResourceOwner.class));

        RegisterException actual = null;
        try {
            subject.run(email, password, repeatPassword, baseURI);
        } catch (RegisterException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(dre));
        assertThat(actual.getRegisterError(), is(RegisterError.EMAIL_TAKEN));
    }

    @Test
    public void runPasswordMismatchShouldThrowRegisterException() throws Exception {
        String email = "obi-wan@tokensmith.net";
        String password = "password";
        String repeatPassword = "mismatchPassword";

        RegisterException actual = null;
        try {
            subject.run(email, password, repeatPassword, BASE_URI);
        } catch (RegisterException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(nullValue()));
        assertThat(actual.getRegisterError(), is(RegisterError.PASSWORD_MISMATCH));
    }
}