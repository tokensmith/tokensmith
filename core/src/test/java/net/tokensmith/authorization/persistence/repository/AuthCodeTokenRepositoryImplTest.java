package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.repository.repo.AuthCodeTokenRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.repository.entity.AuthCodeToken;
import net.tokensmith.repository.exceptions.DuplicateRecordException;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.authorization.persistence.mapper.AuthCodeTokenMapper;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by tommackenzie on 4/16/16.
 */
public class AuthCodeTokenRepositoryImplTest {

    @Mock
    private AuthCodeTokenMapper mockAuthCodeTokenMapper;

    private AuthCodeTokenRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new AuthCodeTokenRepositoryImpl(mockAuthCodeTokenMapper);
    }

    @Test
    public void insertShouldBeOk() throws Exception {
        AuthCodeToken authCodeToken = new AuthCodeToken();

        subject.insert(authCodeToken);

        verify(mockAuthCodeTokenMapper, times(1)).insert(authCodeToken);
    }

    @Test(expected = DuplicateRecordException.class)
    public void insertDuplicateAuthCodeId() throws DuplicateRecordException {
        AuthCodeToken authCodeToken = new AuthCodeToken();
        doThrow(org.springframework.dao.DuplicateKeyException.class).when(mockAuthCodeTokenMapper).insert(authCodeToken);

        subject.insert(authCodeToken);
    }

    @Test
    public void getByTokenIdShouldBeOk() throws Exception{
        UUID tokenId = UUID.randomUUID();
        AuthCodeToken authCodeToken = new AuthCodeToken();

        when(mockAuthCodeTokenMapper.getByTokenId(tokenId)).thenReturn(authCodeToken);

        AuthCodeToken actual = subject.getByTokenId(tokenId);
        assertThat(actual, is(authCodeToken));
    }

    @Test(expected = RecordNotFoundException.class)
    public void getByTokenIdShouldThrowRecordNotFound() throws Exception {
        UUID tokenId = UUID.randomUUID();

        when(mockAuthCodeTokenMapper.getByTokenId(tokenId)).thenReturn(null);

        subject.getByTokenId(tokenId);
    }
}