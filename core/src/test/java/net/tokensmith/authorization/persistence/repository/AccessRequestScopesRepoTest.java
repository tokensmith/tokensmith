package net.tokensmith.authorization.persistence.repository;

import helper.fixture.FixtureFactory;
import net.tokensmith.repository.repo.AccessRequestScopesRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.repository.entity.AccessRequestScope;
import net.tokensmith.repository.entity.Scope;
import net.tokensmith.authorization.persistence.mapper.AccessRequestScopesMapper;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by tommackenzie on 5/19/15.
 */
public class AccessRequestScopesRepoTest {

    @Mock
    private AccessRequestScopesMapper mockAccessRequestScopesMapper;

    private AccessRequestScopesRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new AccessRequestScopesRepo(mockAccessRequestScopesMapper);
    }

    @Test
    public void testInsert() throws Exception {
        List<Scope> scopes = FixtureFactory.makeScopes();

        AccessRequestScope accessRequestScope = new AccessRequestScope(
                UUID.randomUUID(), UUID.randomUUID(), scopes.get(0)
        );

        subject.insert(accessRequestScope);
        verify(mockAccessRequestScopesMapper, times(1)).insert(accessRequestScope);
    }
}