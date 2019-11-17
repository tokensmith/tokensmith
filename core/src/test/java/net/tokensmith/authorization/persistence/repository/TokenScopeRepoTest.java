package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.repository.repo.TokenScopeRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.repository.entity.TokenScope;
import net.tokensmith.authorization.persistence.mapper.TokenScopeMapper;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by tommackenzie on 4/18/16.
 */
public class TokenScopeRepoTest {

    @Mock
    private TokenScopeMapper mockTokenScopeMapper;

    private TokenScopeRepository subject;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        subject = new TokenScopeRepo(mockTokenScopeMapper);
    }

    @Test
    public void insertShouldBeOk() {
        TokenScope tokenScope = new TokenScope();

        subject.insert(tokenScope);

        verify(mockTokenScopeMapper, times(1)).insert(tokenScope);
    }

}