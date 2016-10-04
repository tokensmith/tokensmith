package org.rootservices.authorization.persistence.repository;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.RefreshToken;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.mapper.RefreshTokenMapper;
import org.springframework.dao.DuplicateKeyException;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by tommackenzie on 10/3/16.
 */
public class RefreshTokenRepositoryImplTest {
    private RefreshTokenRepository subject;
    @Mock
    private RefreshTokenMapper mockRefreshTokenMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new RefreshTokenRepositoryImpl(mockRefreshTokenMapper);
    }

    @Test
    public void insertShouldBeOk() throws Exception {
        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(UUID.randomUUID());

        subject.insert(refreshToken);
        verify(mockRefreshTokenMapper, times(1)).insert(refreshToken);
    }

    @Test
    public void insertShouldThrowDuplicateRecordException() throws Exception {
        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(UUID.randomUUID());

        DuplicateKeyException dke = new DuplicateKeyException("");
        doThrow(dke).when(mockRefreshTokenMapper).insert(any(RefreshToken.class));

        DuplicateRecordException actual = null;
        try {
            subject.insert(refreshToken);
        } catch (DuplicateRecordException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getDomainCause(), is(dke));
    }

    @Test
    public void getByTokenShouldBeOk() {
        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(UUID.randomUUID());
        String accessToken = new String(refreshToken.getToken());
        when(mockRefreshTokenMapper.getByToken(accessToken)).thenReturn(refreshToken);

        RefreshToken actual = subject.getByToken(accessToken);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(refreshToken));
    }
}