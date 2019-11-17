package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.repository.repo.AccessRequestRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.repository.entity.AccessRequest;
import net.tokensmith.authorization.persistence.mapper.AccessRequestMapper;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Created by tommackenzie on 4/15/15.
 */
public class AccessRequestRepoTest {

    @Mock
    private AccessRequestMapper mockAuthRequestMapper;

    private AccessRequestRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new AccessRequestRepo(mockAuthRequestMapper);
    }

    @Test
    public void insert() throws Exception {
        AccessRequest accessRequest = new AccessRequest();
        subject.insert(accessRequest);
        verify(mockAuthRequestMapper).insert(accessRequest);
    }
}