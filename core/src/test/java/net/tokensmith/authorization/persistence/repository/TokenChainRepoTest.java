package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.repository.repo.TokenChainRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.repository.entity.TokenChain;
import net.tokensmith.repository.exceptions.DuplicateRecordException;
import net.tokensmith.authorization.persistence.mapper.TokenChainMapper;
import org.springframework.dao.DuplicateKeyException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by tommackenzie on 10/8/16.
 */
public class TokenChainRepoTest {
    private TokenChainRepository subject;
    @Mock
    private TokenChainMapper mockTokenChainMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new TokenChainRepo(mockTokenChainMapper);
    }

    @Test
    public void insertShouldBeOk() throws Exception {
        TokenChain tokenChain = new TokenChain();
        subject.insert(tokenChain);
        verify(mockTokenChainMapper, times(1)).insert(tokenChain);
    }

    @Test
    public void insertShouldThrowDuplicateRecordException() throws Exception {
        DuplicateRecordException actual = null;
        DuplicateKeyException dke = new DuplicateKeyException("");
        TokenChain tokenChain = new TokenChain();

        doThrow(dke).when(mockTokenChainMapper).insert(tokenChain);

        try {
            subject.insert(tokenChain);
        } catch (DuplicateRecordException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("Could not insert token chain - refresh token was already used"));
        assertThat(actual.getCause(), instanceOf(DuplicateKeyException.class));
    }
}