package org.rootservices.authorization.persistence.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.ResourceOwnerToken;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.ResourceOwnerTokenMapper;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 4/28/16.
 */
public class ResourceOwnerTokenRepositoryImplTest {
    @Mock
    private ResourceOwnerTokenMapper mockResourceOwnerTokenMapper;

    private ResourceOwnerTokenRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new ResourceOwnerTokenRepositoryImpl(mockResourceOwnerTokenMapper);
    }

    @Test
    public void getByAccessTokenShouldBeOk() throws Exception {
        ResourceOwnerToken resourceOwnerToken = new ResourceOwnerToken();

        when(mockResourceOwnerTokenMapper.getByAccessToken("token".getBytes())).thenReturn(resourceOwnerToken);

        ResourceOwnerToken actual = subject.getByAccessToken("token");

        assertThat(actual, is(resourceOwnerToken));
    }

    @Test(expected = RecordNotFoundException.class)
    public void getByAccessTokenShouldThrowRecordNotFound() throws Exception {
        when(mockResourceOwnerTokenMapper.getByAccessToken("token".getBytes())).thenReturn(null);

        subject.getByAccessToken("token");
    }

    @Test
    public void insertShouldBeOk() {
        ResourceOwnerToken resourceOwnerToken = new ResourceOwnerToken();

        subject.insert(resourceOwnerToken);

        verify(mockResourceOwnerTokenMapper, times(1)).insert(resourceOwnerToken);
    }
}