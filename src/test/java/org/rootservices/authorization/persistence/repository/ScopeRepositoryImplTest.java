package org.rootservices.authorization.persistence.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.persistence.entity.Scope;
import org.rootservices.authorization.persistence.mapper.ScopeMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    public void findByName() throws Exception {
        List<String> names = new ArrayList();
        names.add("profile");

        Scope scope = new Scope(UUID.randomUUID(), "profile");
        List<Scope> scopes = new ArrayList<>();
        scopes.add(scope);

        when(mockScopeMapper.findByName(names)).thenReturn(scopes);

        List<Scope> actual = subject.findByName(names);

        verify(mockScopeMapper, times(1)).findByName(names);
        assertThat(actual).isEqualTo(scopes);
    }
}