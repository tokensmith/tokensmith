package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.repository.repo.NonceTypeRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.repository.entity.NonceName;
import net.tokensmith.repository.entity.NonceType;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.authorization.persistence.mapper.NonceTypeMapper;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NonceTypeRepoTest {

    @Mock
    private NonceTypeMapper mockNonceTypeMapper;

    private NonceTypeRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new NonceTypeRepo(mockNonceTypeMapper);
    }

    @Test
    public void insertShouldBeOk() {
        NonceType nonceType = new NonceType();
        subject.insert(nonceType);

        verify(mockNonceTypeMapper).insert(nonceType);
    }

    @Test
    public void getByIdShouldBeOk() throws Exception {
        NonceType nonceType = new NonceType();
        nonceType.setId(UUID.randomUUID());

        when(mockNonceTypeMapper.getById(nonceType.getId())).thenReturn(nonceType);

        NonceType actual = subject.getById(nonceType.getId());

        assertThat(actual, is(nonceType));
    }

    @Test(expected = RecordNotFoundException.class)
    public void getByIdShouldThrowRecordNotFound() throws Exception {
        NonceType nonceType = new NonceType();
        nonceType.setId(UUID.randomUUID());

        when(mockNonceTypeMapper.getById(nonceType.getId())).thenReturn(null);

        subject.getById(nonceType.getId());
    }

    @Test
    public void getByNameShouldBeOk() throws Exception {
        NonceType nonceType = new NonceType();
        nonceType.setName(NonceName.WELCOME.toString());

        when(mockNonceTypeMapper.getByName(nonceType.getName())).thenReturn(nonceType);

        NonceType actual = subject.getByName(NonceName.WELCOME);

        assertThat(actual, is(nonceType));
    }

    @Test(expected = RecordNotFoundException.class)
    public void getByNameShouldThrowRecordNotFound() throws Exception {
        NonceType nonceType = new NonceType();
        nonceType.setName("foo");

        when(mockNonceTypeMapper.getByName(nonceType.getName())).thenReturn(null);

        subject.getById(nonceType.getId());
    }
}