package org.rootservices.authorization.persistence.repository;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.TokenMapper;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by tommackenzie on 5/23/15.
 */
public class TokenRepositoryImplTest {

    @Mock
    private TokenMapper mockTokenMapper;

    private TokenRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new TokenRepositoryImpl(mockTokenMapper);
    }

    @Test
    public void insert() throws DuplicateRecordException {
        Token token = FixtureFactory.makeToken();
        subject.insert(token);
        verify(mockTokenMapper, times(1)).insert(token);
    }

    @Test(expected = DuplicateRecordException.class)
    public void insertDuplicateAuthCode() throws DuplicateRecordException {
        Token token = FixtureFactory.makeToken();
        doThrow(org.springframework.dao.DuplicateKeyException.class).when(mockTokenMapper).insert(token);

        subject.insert(token);
        verify(mockTokenMapper, times(1)).insert(token);
    }

    @Test
    public void revokeByAuthCodeIdShouldBeOk() {
        UUID authCodeId = UUID.randomUUID();

        subject.revokeByAuthCodeId(authCodeId);
        verify(mockTokenMapper).revokeByAuthCodeId(authCodeId);
    }

    @Test
    public void getByAuthCodeIdShouldBeOk() throws Exception {
        Token token = new Token();
        UUID authCodeId = UUID.randomUUID();
        when(mockTokenMapper.getByAuthCodeId(authCodeId)).thenReturn(token);

        Token actual = subject.getByAuthCodeId(authCodeId);
        assertThat(actual, is(token));
    }

    @Test(expected = RecordNotFoundException.class)
    public void getByAuthCodeIdShouldThrowRecordNotFound() throws Exception {

        UUID authCodeId = UUID.randomUUID();
        when(mockTokenMapper.getByAuthCodeId(authCodeId)).thenReturn(null);

        subject.getByAuthCodeId(authCodeId);
    }
}