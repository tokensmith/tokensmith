package org.rootservices.authorization.persistence.repository;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.AccessRequest;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.AccessRequestMapper;

import java.net.URISyntaxException;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 4/15/15.
 */
public class AccessRequestRepositoryImplTest {

    @Mock
    private AccessRequestMapper mockAuthRequestMapper;

    private AccessRequestRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new AccessRequestRepositoryImpl(mockAuthRequestMapper);
    }

    @Test
    public void insert() throws Exception {
        AccessRequest accessRequest = new AccessRequest();
        subject.insert(accessRequest);
        verify(mockAuthRequestMapper).insert(accessRequest);
    }

    @Test
    public void getByAccessTokenShouldReturnAccessRequest() throws URISyntaxException, RecordNotFoundException {
        String accessToken = "accessToken";
        AccessRequest ar = FixtureFactory.makeAccessRequest(UUID.randomUUID(), UUID.randomUUID());

        when(mockAuthRequestMapper.getByAccessToken(accessToken.getBytes())).thenReturn(ar);

        AccessRequest actual = subject.getByAccessToken(accessToken);
        assertThat(actual, is(ar));
    }

    @Test(expected= RecordNotFoundException.class)
    public void getByAccessTokenShouldThrowRecordNotFoundException() throws RecordNotFoundException {
        String accessToken = "accessToken";
        when(mockAuthRequestMapper.getByAccessToken(accessToken.getBytes())).thenReturn(null);

        subject.getByAccessToken(accessToken);
    }
}