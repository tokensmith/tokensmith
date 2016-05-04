package org.rootservices.authorization.persistence.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.TokenScope;
import org.rootservices.authorization.persistence.mapper.TokenScopeMapper;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by tommackenzie on 4/18/16.
 */
public class TokenScopeRepositoryImplTest {

    @Mock
    private TokenScopeMapper mockTokenScopeMapper;

    private TokenScopeRepository subject;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        subject = new TokenScopeRepositoryImpl(mockTokenScopeMapper);
    }

    @Test
    public void insertShouldBeOk() {
        TokenScope tokenScope = new TokenScope();

        subject.insert(tokenScope);

        verify(mockTokenScopeMapper, times(1)).insert(tokenScope);
    }

}