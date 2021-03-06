package net.tokensmith.authorization.nonce.reset;

import helper.fixture.FixtureFactory;
import net.tokensmith.authorization.exception.BadRequestException;
import net.tokensmith.authorization.exception.NotFoundException;
import net.tokensmith.authorization.nonce.InsertNonce;
import net.tokensmith.authorization.nonce.SpendNonce;
import net.tokensmith.authorization.nonce.exception.JwtException;
import net.tokensmith.authorization.register.exception.NonceException;
import net.tokensmith.authorization.security.ciphers.HashTextRandomSalt;
import net.tokensmith.authorization.security.entity.NonceClaim;
import net.tokensmith.jwt.builder.compact.UnsecureCompactBuilder;
import net.tokensmith.jwt.entity.jwt.JsonWebToken;
import net.tokensmith.pelican.Publish;
import net.tokensmith.repository.entity.Nonce;
import net.tokensmith.repository.entity.NonceName;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.repository.repo.RefreshTokenRepository;
import net.tokensmith.repository.repo.ResourceOwnerRepository;
import net.tokensmith.repository.repo.TokenRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class ForgotPasswordTest {
    private static String BASE_URI = "sso.tokensmith.net";

    @Mock
    private InsertNonce mockInsertNonce;
    @Mock
    private Publish mockPublish;
    @Mock
    private SpendNonce mockSpendNonce;
    @Mock
    private HashTextRandomSalt mockHashTextRandomSalt;
    @Mock
    private ResourceOwnerRepository mockResourceOwnerRepository;
    @Mock
    private TokenRepository mockTokenRepository;
    @Mock
    private RefreshTokenRepository mockRefreshTokenRepository;
    private ForgotPassword subject;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new ForgotPassword(mockInsertNonce, mockPublish, mockSpendNonce, mockHashTextRandomSalt, mockResourceOwnerRepository, mockTokenRepository, mockRefreshTokenRepository);
    }

    @SuppressWarnings("unchecked")
    public ArgumentCaptor<Map<String, String>> captorForHashMap() {
        return ArgumentCaptor.forClass(Map.class);
    }

    @Test
    public void sendMessageShouldBeOk() throws Exception {
        String email = "obi-wan@tokensmith.net";
        String plainTextNonce = "plainTextNonce";

        when(mockInsertNonce.insert(email, NonceName.RESET_PASSWORD)).thenReturn(plainTextNonce);

        subject.sendMessage(email, BASE_URI);

        ArgumentCaptor<Map<String, String>> messageCaptor = captorForHashMap();
        verify(mockPublish).send(eq("message-user"), messageCaptor.capture());

        assertThat(messageCaptor.getValue().size(), is(4));
        assertThat(messageCaptor.getValue().get("type"), is("forgot_password"));
        assertThat(messageCaptor.getValue().get("recipient"), is(email));
        assertThat(messageCaptor.getValue().get("base_link"), is(BASE_URI + "/update-password?nonce="));
        assertThat(messageCaptor.getValue().get("nonce"), is(plainTextNonce));
    }

    @Test
    public void sendMessageWhenEmailEmptyShouldThrowBadRequestException() throws Exception {
        String email = "";

        BadRequestException actual = null;
        try {
            subject.sendMessage(email, BASE_URI);
        } catch (BadRequestException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getField(), is("email"));
        assertThat(actual.getDescription(), is("Email is required"));
        verify(mockPublish, never()).send(eq("message-user"), anyMap());
    }

    @Test
    public void sendMessageWhenEmailWhiteSpaceShouldThrowBadRequestException() throws Exception {
        String email = " ";

        BadRequestException actual = null;
        try {
            subject.sendMessage(email, BASE_URI);
        } catch (BadRequestException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getField(), is("email"));
        assertThat(actual.getDescription(), is("Email is required"));
        verify(mockPublish, never()).send(eq("message-user"), anyMap());
    }

    @Test
    public void sendMessageWhenEmailNullShouldThrowBadRequestException() throws Exception {
        String email = null;

        BadRequestException actual = null;
        try {
            subject.sendMessage(email, BASE_URI);
        } catch (BadRequestException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getField(), is("email"));
        assertThat(actual.getDescription(), is("Email is required"));
        verify(mockPublish, never()).send(eq("message-user"), anyMap());
    }

    @Test
    public void sendMessageShouldThrowNonceException() throws Exception {
        String email = "obi-wan@tokensmith.net";

        when(mockInsertNonce.insert(email, NonceName.RESET_PASSWORD)).thenThrow(NonceException.class);

        NonceException actual = null;
        try {
            subject.sendMessage(email, BASE_URI);
        } catch (NonceException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        verify(mockPublish, never()).send(eq("message-user"), anyMap());
    }

    @Test
    public void resetShouldBeOk() throws Exception {
        Nonce nonce = new Nonce();
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        nonce.setResourceOwner(ro);

        String jwt = "some.jwt";
        String password = "plainTextPassword";
        String repeatPassword = "plainTextPassword";
        String hashedPassword = "hashedPassword";

        when(mockSpendNonce.spend(jwt, NonceName.RESET_PASSWORD)).thenReturn(nonce);
        when(mockHashTextRandomSalt.run(password)).thenReturn(hashedPassword);

        subject.reset(jwt, password, repeatPassword);

        verify(mockResourceOwnerRepository).updatePassword(nonce.getResourceOwner().getId(), hashedPassword);
        verify(mockTokenRepository).revokeActive(nonce.getResourceOwner().getId());
        verify(mockRefreshTokenRepository).revokeActive(nonce.getResourceOwner().getId());

        ArgumentCaptor<Map<String, String>> messageCaptor = captorForHashMap();
        verify(mockPublish).send(eq("message-user"), messageCaptor.capture());

        assertThat(messageCaptor.getValue().size(), is(2));
        assertThat(messageCaptor.getValue().get("type"), is("password_was_reset"));
        assertThat(messageCaptor.getValue().get("recipient"), is(nonce.getResourceOwner().getEmail()));
    }

    @Test
    public void resetWhenJwtEmptyShouldThrowBadRequestException() throws Exception {
        Nonce nonce = new Nonce();
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        nonce.setResourceOwner(ro);

        String jwt = "";
        String password = "plainTextPassword";
        String repeatPassword = "plainTextPassword";

        when(mockSpendNonce.spend(jwt, NonceName.RESET_PASSWORD)).thenReturn(nonce);

        BadRequestException actual = null;
        try {
            subject.reset(jwt, password, repeatPassword);
        } catch (BadRequestException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getField(), is("nonce"));
        assertThat(actual.getDescription(), is("Nonce is required"));

        verify(mockSpendNonce, never()).spend(jwt, NonceName.RESET_PASSWORD);
        verify(mockHashTextRandomSalt, never()).run(password);
        verify(mockResourceOwnerRepository, never()).updatePassword(any(UUID.class), anyString());
        verify(mockTokenRepository, never()).revokeActive(any(UUID.class));
        verify(mockRefreshTokenRepository, never()).revokeActive(any(UUID.class));
        verify(mockPublish, never()).send(eq("message-user"), anyMap());
    }

    @Test
    public void resetWhenJwtNullShouldThrowBadRequestException() throws Exception {
        Nonce nonce = new Nonce();
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        nonce.setResourceOwner(ro);

        String jwt = null;
        String password = "plainTextPassword";
        String repeatPassword = "plainTextPassword";

        when(mockSpendNonce.spend(jwt, NonceName.RESET_PASSWORD)).thenReturn(nonce);

        BadRequestException actual = null;
        try {
            subject.reset(jwt, password, repeatPassword);
        } catch (BadRequestException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getField(), is("nonce"));
        assertThat(actual.getDescription(), is("Nonce is required"));

        verify(mockSpendNonce, never()).spend(jwt, NonceName.RESET_PASSWORD);
        verify(mockHashTextRandomSalt, never()).run(password);
        verify(mockResourceOwnerRepository, never()).updatePassword(any(UUID.class), anyString());
        verify(mockTokenRepository, never()).revokeActive(any(UUID.class));
        verify(mockRefreshTokenRepository, never()).revokeActive(any(UUID.class));
        verify(mockPublish, never()).send(eq("message-user"), anyMap());
    }

    @Test
    public void resetWhenBadNonceShouldThrowBadRequestException() throws Exception {
        Nonce nonce = new Nonce();
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        nonce.setResourceOwner(ro);

        String jwt = "some.jwt";
        String password = "plainTextPassword";
        String repeatPassword = "plainTextPassword";

        when(mockSpendNonce.spend(jwt, NonceName.RESET_PASSWORD)).thenThrow(JwtException.class);

        BadRequestException actual = null;
        try {
            subject.reset(jwt, password, repeatPassword);
        } catch (BadRequestException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getField(), is("nonce"));

        verify(mockHashTextRandomSalt, never()).run(password);
        verify(mockResourceOwnerRepository, never()).updatePassword(any(UUID.class), anyString());
        verify(mockTokenRepository, never()).revokeActive(any(UUID.class));
        verify(mockRefreshTokenRepository, never()).revokeActive(any(UUID.class));
        verify(mockPublish, never()).send(eq("message-user"), anyMap());
    }

    @Test
    public void resetWhenPasswordEmptyShouldThrowBadRequestException() throws Exception {
        Nonce nonce = new Nonce();
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        nonce.setResourceOwner(ro);

        String jwt = "some.jwt";
        String password = "";
        String repeatPassword = "plainTextPassword";

        when(mockSpendNonce.spend(jwt, NonceName.RESET_PASSWORD)).thenReturn(nonce);

        BadRequestException actual = null;
        try {
            subject.reset(jwt, password, repeatPassword);
        } catch (BadRequestException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getField(), is("password"));
        assertThat(actual.getDescription(), is("Password is required"));

        verify(mockSpendNonce, never()).spend(jwt, NonceName.RESET_PASSWORD);
        verify(mockHashTextRandomSalt, never()).run(password);
        verify(mockResourceOwnerRepository, never()).updatePassword(any(UUID.class), anyString());
        verify(mockTokenRepository, never()).revokeActive(any(UUID.class));
        verify(mockRefreshTokenRepository, never()).revokeActive(any(UUID.class));
        verify(mockPublish, never()).send(eq("message-user"), anyMap());
    }

    @Test
    public void resetWhenPasswordNullShouldThrowBadRequestException() throws Exception {
        Nonce nonce = new Nonce();
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        nonce.setResourceOwner(ro);

        String jwt = "some.jwt";
        String password = null;
        String repeatPassword = "plainTextPassword";

        when(mockSpendNonce.spend(jwt, NonceName.RESET_PASSWORD)).thenReturn(nonce);

        BadRequestException actual = null;
        try {
            subject.reset(jwt, password, repeatPassword);
        } catch (BadRequestException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getField(), is("password"));
        assertThat(actual.getDescription(), is("Password is required"));

        verify(mockSpendNonce, never()).spend(jwt, NonceName.RESET_PASSWORD);
        verify(mockHashTextRandomSalt, never()).run(password);
        verify(mockResourceOwnerRepository, never()).updatePassword(any(UUID.class), anyString());
        verify(mockTokenRepository, never()).revokeActive(any(UUID.class));
        verify(mockRefreshTokenRepository, never()).revokeActive(any(UUID.class));
        verify(mockPublish, never()).send(eq("message-user"), anyMap());
    }

    @Test
    public void resetWhenRepeatPasswordEmptyShouldThrowBadRequestException() throws Exception {
        Nonce nonce = new Nonce();
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        nonce.setResourceOwner(ro);

        String jwt = "some.jwt";
        String password = "plainTextPassword";
        String repeatPassword = "";

        when(mockSpendNonce.spend(jwt, NonceName.RESET_PASSWORD)).thenReturn(nonce);

        BadRequestException actual = null;
        try {
            subject.reset(jwt, password, repeatPassword);
        } catch (BadRequestException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getField(), is("repeatPassword"));
        assertThat(actual.getDescription(), is("Repeat Password is required"));

        verify(mockSpendNonce, never()).spend(jwt, NonceName.RESET_PASSWORD);
        verify(mockHashTextRandomSalt, never()).run(password);
        verify(mockResourceOwnerRepository, never()).updatePassword(any(UUID.class), anyString());
        verify(mockTokenRepository, never()).revokeActive(any(UUID.class));
        verify(mockRefreshTokenRepository, never()).revokeActive(any(UUID.class));
        verify(mockPublish, never()).send(eq("message-user"), anyMap());
    }

    @Test
    public void resetWhenRepeatPasswordNullShouldThrowBadRequestException() throws Exception {
        Nonce nonce = new Nonce();
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        nonce.setResourceOwner(ro);

        String jwt = "some.jwt";
        String password = "plainTextPassword";
        String repeatPassword = null;

        when(mockSpendNonce.spend(jwt, NonceName.RESET_PASSWORD)).thenReturn(nonce);

        BadRequestException actual = null;
        try {
            subject.reset(jwt, password, repeatPassword);
        } catch (BadRequestException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getField(), is("repeatPassword"));
        assertThat(actual.getDescription(), is("Repeat Password is required"));

        verify(mockSpendNonce, never()).spend(jwt, NonceName.RESET_PASSWORD);
        verify(mockHashTextRandomSalt, never()).run(password);
        verify(mockResourceOwnerRepository, never()).updatePassword(any(UUID.class), anyString());
        verify(mockTokenRepository, never()).revokeActive(any(UUID.class));
        verify(mockRefreshTokenRepository, never()).revokeActive(any(UUID.class));
        verify(mockPublish, never()).send(eq("message-user"), anyMap());
    }

    @Test
    public void resetWhenPasswordsDontMatchShouldThrowBadRequestException() throws Exception {
        Nonce nonce = new Nonce();
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        nonce.setResourceOwner(ro);

        String jwt = "some.jwt";
        String password = "password1";
        String repeatPassword = "password2";

        when(mockSpendNonce.spend(jwt, NonceName.RESET_PASSWORD)).thenReturn(nonce);

        BadRequestException actual = null;
        try {
            subject.reset(jwt, password, repeatPassword);
        } catch (BadRequestException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getField(), is("password"));
        assertThat(actual.getDescription(), is("Passwords do not match"));

        verify(mockSpendNonce, never()).spend(jwt, NonceName.RESET_PASSWORD);
        verify(mockHashTextRandomSalt, never()).run(password);
        verify(mockResourceOwnerRepository, never()).updatePassword(any(UUID.class), anyString());
        verify(mockTokenRepository, never()).revokeActive(any(UUID.class));
        verify(mockRefreshTokenRepository, never()).revokeActive(any(UUID.class));
        verify(mockPublish, never()).send(eq("message-user"), anyMap());
    }


    @Test
    public void resetShouldThrowNotFoundException() throws Exception {
        Nonce nonce = new Nonce();
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        nonce.setResourceOwner(ro);

        String jwt = "some.jwt";
        String password = "plainTextPassword";
        String repeatPassword = "plainTextPassword";

        when(mockSpendNonce.spend(jwt, NonceName.RESET_PASSWORD)).thenThrow(NotFoundException.class);

        NotFoundException actual = null;
        try {
            subject.reset(jwt, password, repeatPassword);
        } catch (NotFoundException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));

        verify(mockHashTextRandomSalt, never()).run(password);
        verify(mockResourceOwnerRepository, never()).updatePassword(any(UUID.class), anyString());
        verify(mockTokenRepository, never()).revokeActive(any(UUID.class));
        verify(mockRefreshTokenRepository, never()).revokeActive(any(UUID.class));
        verify(mockPublish, never()).send(eq("message-user"), anyMap());
    }

    @Test
    public void toJwtShouldBeOk() throws Exception {
        String plainTextNonce = "1234";
        NonceClaim nonceClaim = new NonceClaim();
        nonceClaim.setNonce(plainTextNonce);
        UnsecureCompactBuilder compactBuilder = new UnsecureCompactBuilder();
        String jwt = compactBuilder.claims(nonceClaim).build().toString();

        JsonWebToken<NonceClaim> actual = subject.toJwt(jwt);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getClaims().getNonce(), is(plainTextNonce));
    }

    @Test
    public void toJwtShouldThrowJwtException() throws Exception {
        String jwt = "not-a-jwt";

        JwtException actual = null;
        try {
            subject.toJwt(jwt);
        } catch (JwtException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void verifyNonceShouldBeOk() throws Exception {
        String plainTextNonce = "1234";
        NonceClaim nonceClaim = new NonceClaim();
        nonceClaim.setNonce(plainTextNonce);
        UnsecureCompactBuilder compactBuilder = new UnsecureCompactBuilder();
        String jwt = compactBuilder.claims(nonceClaim).build().toString();

        boolean actual = subject.verifyNonce(jwt);

        assertThat(actual, is(true));
    }

    @Test
    public void verifyNonceShouldThrowNonceException() throws Exception {
        String jwt = "not-a-jwt";

        NonceException actual = null;
        try {
            subject.verifyNonce(jwt);
        } catch (NonceException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void hasValueShouldBeTrue() {
        Boolean actual = subject.hasValue("some-value");
        assertThat(actual, is(true));
    }

    @Test
    public void hasValueWhenNullShouldBeFalse() {
        Boolean actual = subject.hasValue(null);
        assertThat(actual, is(false));
    }

    @Test
    public void hasValueWhenEmptyShouldBeFalse() {
        Boolean actual = subject.hasValue("");
        assertThat(actual, is(false));
    }

    @Test
    public void hasValueWhenWhiteSpaceShouldBeFalse() {
        Boolean actual = subject.hasValue(" ");
        assertThat(actual, is(false));
    }



}