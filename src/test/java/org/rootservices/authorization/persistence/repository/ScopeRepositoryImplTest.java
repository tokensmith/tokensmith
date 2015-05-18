package org.rootservices.authorization.persistence.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.persistence.entity.Scope;
import org.rootservices.authorization.persistence.mapper.ScopeMapper;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by tommackenzie on 5/12/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class ScopeRepositoryImplTest {

    @Mock
    private ScopeMapper mockScopeMapper;

    private ScopeRepository subject;

    @Before
    public void setUp() {
        subject = new ScopeRepositoryImpl(mockScopeMapper);
    }

    @Test
    public void insert() throws Exception {
        Scope scope = new Scope(UUID.randomUUID(), "profile");
        subject.insert(scope);
        verify(mockScopeMapper, times(1)).insert(scope);
    }
}