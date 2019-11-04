package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.repository.repo.ScopeRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.repository.entity.Scope;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.authorization.persistence.mapper.ScopeMapper;

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
    public void findByNames() throws Exception {
        List<String> names = new ArrayList<>();
        names.add("profile");

        Scope scope = new Scope(UUID.randomUUID(), "profile");
        List<Scope> scopes = new ArrayList<>();
        scopes.add(scope);

        when(mockScopeMapper.findByNames(names)).thenReturn(scopes);

        List<Scope> actual = subject.findByNames(names);

        verify(mockScopeMapper, times(1)).findByNames(names);
        assertThat(actual, is(scopes));
    }

    @Test
    public void findByNamesWhenInputIsNullShouldReturnEmptyList() throws Exception {
        List<String> names = null;

        List<Scope> actual = subject.findByNames(names);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(0));
    }

    @Test
    public void findByNamesWhenInputIsEmptyListShouldReturnEmptyList() throws Exception {
        List<String> names = new ArrayList<>();

        List<Scope> actual = subject.findByNames(names);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(0));
    }

    @Test
    public void findByNameShouldBeOk() throws Exception {
        String scopeName = "profile";

        Scope scope = new Scope(UUID.randomUUID(), scopeName);


        when(mockScopeMapper.findByName(scopeName)).thenReturn(scope);

        Scope actual = subject.findByName(scopeName);

        verify(mockScopeMapper, times(1)).findByName(scopeName);
        assertThat(actual, is(scope));
    }

    @Test(expected = RecordNotFoundException.class)
    public void findByNameShouldThrowRecordNotFound() throws Exception {
        String scopeName = "profile";

        when(mockScopeMapper.findByName(scopeName)).thenReturn(null);

        subject.findByName(scopeName);
    }
}