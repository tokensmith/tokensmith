package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.repository.repo.GivenNameRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.repository.entity.GivenName;
import net.tokensmith.authorization.persistence.mapper.GivenNameMapper;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by tommackenzie on 5/16/17.
 */
public class GivenNameRepositoryImplTest {

    @Mock
    private GivenNameMapper mockGivenNameMapper;
    private GivenNameRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new GivenNameRepositoryImpl(mockGivenNameMapper);
    }

    @Test
    public void testInsertShouldBeOk() {
        GivenName givenName = new GivenName();
        subject.insert(givenName);

        verify(mockGivenNameMapper, times(1)).insert(givenName);
    }

}