package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.AddressRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.repository.entity.Address;
import net.tokensmith.authorization.persistence.mapper.AddressMapper;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 5/11/17.
 */
public class AddressRepoTest {

    @Mock
    private AddressMapper mockAddressMapper;
    private AddressRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new AddressRepo(mockAddressMapper);
    }

    @Test
    public void testInsertShouldBeOk() {
        Address address = new Address();
        subject.insert(address);

        verify(mockAddressMapper, times(1)).insert(address);
    }

    @Test
    public void getByIdAndResourceOwnerIdShouldBeOk() throws Exception {
        Address address = new Address();
        UUID resourceOwnerId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();

        when(mockAddressMapper.getByIdAndResourceOwnerId(addressId, resourceOwnerId)).thenReturn(address);

        Address actual = subject.getByIdAndResourceOwnerId(addressId, resourceOwnerId);

        assertThat(address, is(actual));
    }

    @Test
    public void getByIdAndResourceOwnerIdWhenNotFoundThenShouldThrowRecordNotFound() throws Exception {
        UUID resourceOwnerId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();
        when(mockAddressMapper.getByIdAndResourceOwnerId(addressId, resourceOwnerId)).thenReturn(null);

        RecordNotFoundException actual = null;
        try {
            subject.getByIdAndResourceOwnerId(addressId, resourceOwnerId);
        } catch (RecordNotFoundException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void updateShouldBeOk() {
        UUID resourceOwnerId = UUID.randomUUID();
        Address address = new Address();

        subject.update(resourceOwnerId, address);

        verify(mockAddressMapper, times(1)).update(resourceOwnerId, address);
    }

    @Test
    public void deleteShouldBeOk() {
        UUID id = UUID.randomUUID();
        UUID resourceOwnerId = UUID.randomUUID();

        subject.delete(id, resourceOwnerId);

        verify(mockAddressMapper, times(1)).delete(id, resourceOwnerId);
    }

}