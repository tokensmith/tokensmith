package org.rootservices.authorization.persistence.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.TokenSize;
import org.rootservices.authorization.persistence.mapper.TokenSizeMapper;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 11/5/16.
 */
public class TokenSizeRepositoryImplTest {
    private TokenSizeRepository subject;

    @Mock
    private TokenSizeMapper mockTokenSizeMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new TokenSizeRepositoryImpl(mockTokenSizeMapper);
    }

    @Test
    public void getShouldBeOk() {
        TokenSize tokenSize = new TokenSize();
        when(mockTokenSizeMapper.get()).thenReturn(tokenSize);

        TokenSize actual = subject.get();

        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void updateAccessTokenSizeShouldBeOk() {
        UUID id = UUID.randomUUID();
        Integer size = 50;

        subject.updateAccessTokenSize(id, size);
        verify(mockTokenSizeMapper, times(1)).updateAccessTokenSize(id, size);
    }

    @Test
    public void updateAuthorizationCodeSizeShouldBeOk() {
        UUID id = UUID.randomUUID();
        Integer size = 50;

        subject.updateAuthorizationCodeSize(id, size);
        verify(mockTokenSizeMapper, times(1)).updateAuthorizationCodeSize(id, size);
    }

    @Test
    public void updateRefreshTokenSizeShouldBeOk() {
        UUID id = UUID.randomUUID();
        Integer size = 50;

        subject.updateRefreshTokenSize(id, size);
        verify(mockTokenSizeMapper, times(1)).updateRefreshTokenSize(id, size);
    }

}