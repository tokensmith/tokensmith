package org.rootservices.authorization.persistence.repository;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.mapper.TokenMapper;

import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
        Token token = FixtureFactory.makeToken(UUID.randomUUID());
        subject.insert(token);
        verify(mockTokenMapper, times(1)).insert(token);
    }

    @Test(expected = DuplicateRecordException.class)
    public void insertDuplicateAuthCode() throws DuplicateRecordException {
        Token token = FixtureFactory.makeToken(UUID.randomUUID());
        doThrow(org.springframework.dao.DuplicateKeyException.class).when(mockTokenMapper).insert(token);

        subject.insert(token);
        verify(mockTokenMapper, times(1)).insert(token);
    }

    @Test
    public void revoke() {
        Token token = FixtureFactory.makeToken(UUID.randomUUID());

        subject.revoke(token.getAuthCodeUUID());
        verify(mockTokenMapper).revoke(token.getAuthCodeUUID());
    }
}