package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.repository.repo.ResourceOwnerTokenRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.repository.entity.ResourceOwnerToken;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.authorization.persistence.mapper.ResourceOwnerTokenMapper;

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

        when(mockResourceOwnerTokenMapper.getByAccessToken("token")).thenReturn(resourceOwnerToken);

        ResourceOwnerToken actual = subject.getByAccessToken("token");

        assertThat(actual, is(resourceOwnerToken));
    }

    @Test(expected = RecordNotFoundException.class)
    public void getByAccessTokenShouldThrowRecordNotFound() throws Exception {
        when(mockResourceOwnerTokenMapper.getByAccessToken("token")).thenReturn(null);

        subject.getByAccessToken("token");
    }

    @Test
    public void insertShouldBeOk() {
        ResourceOwnerToken resourceOwnerToken = new ResourceOwnerToken();

        subject.insert(resourceOwnerToken);

        verify(mockResourceOwnerTokenMapper, times(1)).insert(resourceOwnerToken);
    }
}