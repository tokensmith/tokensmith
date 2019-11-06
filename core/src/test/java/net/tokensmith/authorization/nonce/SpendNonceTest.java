package net.tokensmith.authorization.nonce;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.authorization.exception.BadRequestException;
import net.tokensmith.authorization.exception.NotFoundException;
import net.tokensmith.repository.entity.NonceName;
import net.tokensmith.repository.entity.Nonce;
import net.tokensmith.repository.entity.NonceType;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.NonceRepository;
import net.tokensmith.authorization.security.ciphers.HashToken;
import net.tokensmith.authorization.security.entity.NonceClaim;
import net.tokensmith.jwt.builder.compact.UnsecureCompactBuilder;
import net.tokensmith.jwt.exception.InvalidJWT;


import java.util.UUID;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SpendNonceTest {
    private SpendNonce subject;

    @Mock
    private HashToken mockHashToken;
    @Mock
    private NonceRepository mockNonceRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new SpendNonce(mockHashToken, mockNonceRepository);
    }

    @Test
    public void spendShouldBeOK() throws Exception {
        // make a jwt for the test.
        UnsecureCompactBuilder compactBuilder = new UnsecureCompactBuilder();

        NonceClaim nonceClaim = new NonceClaim();
        nonceClaim.setNonce("nonce");

        String jwt = compactBuilder.claims(nonceClaim).build().toString();

        NonceType nonceType = new NonceType();
        nonceType.setName("welcome");

        Nonce nonce = new Nonce();
        nonce.setId(UUID.randomUUID());
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        nonce.setResourceOwner(ro);
        nonce.setNonceType(nonceType);

        when(mockHashToken.run("nonce")).thenReturn("hashedNonce");
        when(mockNonceRepository.getByTypeAndNonce(NonceName.WELCOME, "hashedNonce")).thenReturn(nonce);

        subject.spend(jwt, NonceName.WELCOME);

        verify(mockNonceRepository).setSpent(nonce.getId());
        verify(mockNonceRepository).revokeUnSpent(nonce.getNonceType().getName(), nonce.getResourceOwner().getId());
    }

    @Test
    public void spendShouldThrowBadRequestExceptionException() throws Exception {

        BadRequestException actual = null;
        try {
            subject.spend("notAJwt", NonceName.WELCOME);
        } catch(BadRequestException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), instanceOf(InvalidJWT.class));

        verify(mockNonceRepository, never()).setSpent(any(UUID.class));
        verify(mockNonceRepository, never()).revokeUnSpent(any(String.class), any(UUID.class));
    }

    @Test
    public void spendShouldThrowNotFoundExceptionException() throws Exception {
        // make a jwt for the test.
        UnsecureCompactBuilder compactBuilder = new UnsecureCompactBuilder();

        NonceClaim nonceClaim = new NonceClaim();
        nonceClaim.setNonce("nonce");

        String jwt = compactBuilder.claims(nonceClaim).build().toString();

        when(mockHashToken.run("nonce")).thenReturn("hashedNonce");
        when(mockNonceRepository.getByTypeAndNonce(NonceName.WELCOME, "hashedNonce")).thenThrow(RecordNotFoundException.class);

        NotFoundException actual = null;
        try {
            subject.spend(jwt, NonceName.WELCOME);
        } catch(NotFoundException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), instanceOf(RecordNotFoundException.class));

        verify(mockNonceRepository, never()).setSpent(any(UUID.class));
        verify(mockNonceRepository, never()).revokeUnSpent(any(String.class), any(UUID.class));
    }

}