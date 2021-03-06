package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.mapper.TokenLeadTokenMapper;
import net.tokensmith.repository.entity.TokenLeadToken;
import net.tokensmith.repository.repo.TokenLeadTokenRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by tommackenzie on 12/1/16.
 */
public class TokenLeadTokenRepoTest {
    private TokenLeadTokenRepository subject;
    @Mock
    private TokenLeadTokenMapper mockTokenLeadTokenMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new TokenLeadTokenRepo(mockTokenLeadTokenMapper);
    }

    @Test
    public void insertShouldBeOk() {
        TokenLeadToken tokenLeadToken = new TokenLeadToken();
        subject.insert(tokenLeadToken);

        verify(mockTokenLeadTokenMapper, times(1)).insert(tokenLeadToken);
    }
}