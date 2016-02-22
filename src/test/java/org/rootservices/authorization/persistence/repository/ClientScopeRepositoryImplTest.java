package org.rootservices.authorization.persistence.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.ClientScope;
import org.rootservices.authorization.persistence.mapper.ClientScopesMapper;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * Created by tommackenzie on 5/13/15.
 */
public class ClientScopeRepositoryImplTest {

    @Mock
    private ClientScopesMapper mockClientScopeMapper;

    private ClientScopesRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new ClientScopeRepositoryImpl(mockClientScopeMapper);
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