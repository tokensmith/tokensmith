package org.rootservices.authorization.persistence.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.Scope;
import org.rootservices.authorization.persistence.mapper.ScopeMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by tommackenzie on 5/12/15.
 */
public class ScopeRepositoryImplTest {

    @Mock
    private ScopeMapper mockScopeMapper;

    private ScopeRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
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
        assertThat(actual, is(scopes));
    }

    @Test
    public void findByNameWhenInputIsNullShouldReturnEmptyList() throws Exception {
        List<String> names = null;

        List<Scope> actual = subject.findByName(names);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(0));
    }

    @Test
    public void findByNameWhenInputIsEmptyListShouldReturnEmptyList() throws Exception {
        List<String> names = new ArrayList<>();

        List<Scope> actual = subject.findByName(names);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(0));
    }
}