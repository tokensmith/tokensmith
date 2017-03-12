package org.rootservices.authorization.register;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.ResourceOwnerRepository;
import org.rootservices.authorization.security.HashTextRandomSalt;
import org.springframework.dao.DuplicateKeyException;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 3/11/17.
 */
public class RegisterTest {
    @Mock
    private ResourceOwnerRepository mockResourceOwnerRepository;
    @Mock
    private HashTextRandomSalt mockHashTextRandomSalt;
    private Register subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new Register(mockResourceOwnerRepository, mockHashTextRandomSalt);
    }

    @Test
    public void makeShouldBeOk() throws Exception {
        String email = "obi-wan@rootservices.org";
        String password = "password";
        String repeatPassword = "password";
        String hashedPassword = "hashedPassword";
        when(mockHashTextRandomSalt.run(password)).thenReturn(hashedPassword);

        ResourceOwner actual = subject.make(email, password, repeatPassword);

        verify(mockResourceOwnerRepository).insert(any(ResourceOwner.class));

        assertThat(actual.getId(), is(notNullValue()));
        assertThat(actual.getEmail(), is(email));
        assertThat(actual.getPassword(), is(hashedPassword.getBytes()));
    }

    @Test
    public void makeWhenEmailAlreadyUsedShouldThrowRegisterException() throws Exception {
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
            subject.make(email, password, repeatPassword);
        } catch (RegisterException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(dre));
        assertThat(actual.getRegisterError(), is(RegisterError.EMAIL_TAKEN));
    }

    @Test
    public void makePasswordMismatchShouldThrowRegisterException() throws Exception {
        String email = "obi-wan@rootservices.org";
        String password = "password";
        String repeatPassword = "mismatchPassword";


        RegisterException actual = null;
        try {
            subject.make(email, password, repeatPassword);
        } catch (RegisterException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(nullValue()));
        assertThat(actual.getRegisterError(), is(RegisterError.PASSWORD_MISMATCH));
    }

}