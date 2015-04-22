package org.rootservices.authorization.persistence.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.persistence.entity.AccessRequest;
import org.rootservices.authorization.persistence.mapper.AccessRequestMapper;

import static org.mockito.Mockito.verify;

/**
 * Created by tommackenzie on 4/15/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class AccessRequestRepositoryImplTest {

    @Mock
    private AccessRequestMapper mockAuthRequestMapper;

    private AccessRequestRepository subject;

    @Before
    public void setUp() {
        subject = new AccessRequestRepositoryImpl(mockAuthRequestMapper);
    }

    @Test
    public void insert() throws Exception {
        AccessRequest accessRequest = new AccessRequest();
        subject.insert(accessRequest);
        verify(mockAuthRequestMapper).insert(accessRequest);
    }
}