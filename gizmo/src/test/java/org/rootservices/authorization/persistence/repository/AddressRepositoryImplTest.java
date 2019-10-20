package org.rootservices.authorization.persistence.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.Address;
import org.rootservices.authorization.persistence.mapper.AddressMapper;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 5/11/17.
 */
public class AddressRepositoryImplTest {

    @Mock
    private AddressMapper mockAddressMapper;
    private AddressRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new AddressRepositoryImpl(mockAddressMapper);
    }

    @Test
    public void testInsertShouldBeOk() {
        Address address = new Address();
        subject.insert(address);

        verify(mockAddressMapper, times(1)).insert(address);
    }

}