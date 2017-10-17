package org.rootservices.authorization.persistence.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.Nonce;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.NonceMapper;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NonceRepositoryImplTest {

    @Mock
    private NonceMapper mockNonceMapper;

    private NonceRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new NonceRepositoryImpl(mockNonceMapper);
    }

    @Test
    public void insertShouldBeOk() {
        Nonce nonce = new Nonce();
        subject.insert(nonce);

        verify(mockNonceMapper).insert(nonce);
    }

    @Test
    public void getByIdShouldBeOk() throws Exception {
        Nonce nonce = new Nonce();
        nonce.setId(UUID.randomUUID());

        when(mockNonceMapper.getById(nonce.getId())).thenReturn(nonce);

        Nonce actual = subject.getById(nonce.getId());

        assertThat(actual, is(nonce));
    }

    @Test(expected = RecordNotFoundException.class)
    public void getByIdShouldThrowRecordNotFound() throws Exception {
        Nonce nonce = new Nonce();
        nonce.setId(UUID.randomUUID());

        when(mockNonceMapper.getById(nonce.getId())).thenReturn(null);

        subject.getById(nonce.getId());
    }

    @Test
    public void getByTypeAndNonceShouldBeOk() throws Exception {
        Nonce nonce = new Nonce();

        when(mockNonceMapper.getByTypeAndNonce("welcome","nonce")).thenReturn(nonce);

        Nonce actual = subject.getByTypeAndNonce("welcome","nonce");

        assertThat(actual, is(nonce));
    }

    @Test(expected = RecordNotFoundException.class)
    public void getByTypeAndNonceShouldThrowRecordNotFound() throws Exception {

        when(mockNonceMapper.getByTypeAndNonce("welcome", "nonce")).thenReturn(null);

        subject.getByTypeAndNonce("welcome", "nonce");
    }

    @Test
    public void getByNonceShouldBeOk() throws Exception {
        Nonce nonce = new Nonce();

        when(mockNonceMapper.getByNonce("nonce")).thenReturn(nonce);

        Nonce actual = subject.getByNonce("nonce");

        assertThat(actual, is(nonce));
    }

    @Test(expected = RecordNotFoundException.class)
    public void getByNonceShouldThrowRecordNotFound() throws Exception {

        when(mockNonceMapper.getByNonce("nonce")).thenReturn(null);

        subject.getByNonce("nonce");
    }


    @Test
    public void revokeUnSpent() {
        UUID resourceOwnerId = UUID.randomUUID();
        String type = "foo";

        subject.revokeUnSpent(type, resourceOwnerId);

        verify(mockNonceMapper).revokeUnSpent(type, resourceOwnerId);
    }

    @Test
    public void setSpent() {
        UUID id = UUID.randomUUID();

        subject.setSpent(id);

        verify(mockNonceMapper).setSpent(id);
    }

}