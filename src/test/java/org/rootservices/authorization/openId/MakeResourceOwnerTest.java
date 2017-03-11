package org.rootservices.authorization.openId;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.ResourceOwnerRepository;
import org.rootservices.authorization.security.HashTextRandomSalt;

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
public class MakeResourceOwnerTest {
    @Mock
    private ResourceOwnerRepository mockResourceOwnerRepository;
    @Mock
    private HashTextRandomSalt mockHashTextRandomSalt;
    private MakeResourceOwner subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new MakeResourceOwner(mockResourceOwnerRepository, mockHashTextRandomSalt);
    }

    @Test
    public void makeShouldBeOk() throws Exception {
        String email = "obi-wan@rootservices.org";
        String password = "password";
        String hashedPassword = "hashedPassword";
        when(mockHashTextRandomSalt.run(password)).thenReturn(hashedPassword);

        ResourceOwner actual = subject.make(email, password);

        verify(mockResourceOwnerRepository).insert(any(ResourceOwner.class));

        assertThat(actual.getId(), is(notNullValue()));
        assertThat(actual.getEmail(), is(email));
        assertThat(actual.getPassword(), is(hashedPassword.getBytes()));
    }

    @Test(expected = DuplicateRecordException.class)
    public void makeShouldThrowDuplicateRecordException() throws Exception {
        String email = "obi-wan@rootservices.org";
        String password = "password";
        String hashedPassword = "hashedPassword";
        when(mockHashTextRandomSalt.run(password)).thenReturn(hashedPassword);

        doThrow(DuplicateRecordException.class).when(mockResourceOwnerRepository).insert(any(ResourceOwner.class));

        subject.make(email, password);
    }

}