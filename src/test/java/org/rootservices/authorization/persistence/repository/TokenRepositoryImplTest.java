package org.rootservices.authorization.persistence.repository;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.persistence.mapper.TokenMapper;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by tommackenzie on 5/23/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class TokenRepositoryImplTest {

    @Mock
    private TokenMapper mockTokenMapper;

    private TokenRepository subject;

    @Before
    public void setUp() {
        subject = new TokenRepositoryImpl(mockTokenMapper);
    }

    @Test
    public void insert() {
        Token token = FixtureFactory.makeToken(UUID.randomUUID());
        subject.insert(token);
        verify(mockTokenMapper, times(1)).insert(token);
    }

}