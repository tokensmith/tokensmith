package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.mapper.ClientScopesMapper;
import net.tokensmith.repository.entity.ClientScope;
import net.tokensmith.repository.repo.ClientScopesRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * Created by tommackenzie on 5/13/15.
 */
public class ClientScopeRepoTest {

    @Mock
    private ClientScopesMapper mockClientScopeMapper;

    private ClientScopesRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new ClientScopeRepo(mockClientScopeMapper);
    }

    @Test
    public void insert() throws Exception {
        ClientScope clientScope = new ClientScope(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()
        );
        subject.insert(clientScope);
        verify(mockClientScopeMapper, times(1)).insert(clientScope);
    }
}