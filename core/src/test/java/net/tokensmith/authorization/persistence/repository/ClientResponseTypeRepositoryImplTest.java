package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.repository.repo.ClientResponseTypeRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.repository.entity.ClientResponseType;
import net.tokensmith.authorization.persistence.mapper.ClientResponseTypeMapper;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by tommackenzie on 8/9/16.
 */
public class ClientResponseTypeRepositoryImplTest {
    @Mock
    private ClientResponseTypeMapper mockClientResponseTypeMapper;
    private ClientResponseTypeRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new ClientResponseTypeRepositoryImpl(mockClientResponseTypeMapper);
    }

    @Test
    public void testInsert() throws Exception {
        ClientResponseType clientResponseType = new ClientResponseType();

        subject.insert(clientResponseType);
        verify(mockClientResponseTypeMapper, times(1)).insert(clientResponseType);
    }
}