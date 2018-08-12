package org.rootservices.authorization.nonce;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.exception.BadRequestException;
import org.rootservices.authorization.exception.NotFoundException;
import org.rootservices.authorization.nonce.entity.NonceName;
import org.rootservices.authorization.persistence.entity.Nonce;
import org.rootservices.authorization.persistence.entity.NonceType;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.NonceRepository;
import org.rootservices.authorization.security.ciphers.HashTextStaticSalt;
import org.rootservices.authorization.security.entity.NonceClaim;
import org.rootservices.jwt.builder.compact.UnsecureCompactBuilder;
import org.rootservices.jwt.exception.InvalidJWT;


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
    private HashTextStaticSalt mockHashTextStaticSalt;
    @Mock
    private NonceRepository mockNonceRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new SpendNonce(mockHashTextStaticSalt, mockNonceRepository);
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

        when(mockHashTextStaticSalt.run("nonce")).thenReturn("hashedNonce");
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

        when(mockHashTextStaticSalt.run("nonce")).thenReturn("hashedNonce");
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