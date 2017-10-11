package org.rootservices.authorization.persistence.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.NonceType;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.NonceTypeMapper;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NonceTypeRepositoryImplTest {

    @Mock
    private NonceTypeMapper mockNonceTypeMapper;

    private NonceTypeRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new NonceTypeRepositoryImpl(mockNonceTypeMapper);
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
        nonceType.setName("foo");

        when(mockNonceTypeMapper.getByName(nonceType.getName())).thenReturn(nonceType);

        NonceType actual = subject.getByName(nonceType.getName());

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