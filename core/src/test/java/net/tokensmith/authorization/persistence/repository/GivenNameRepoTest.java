package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.mapper.GivenNameMapper;
import net.tokensmith.repository.entity.Name;
import net.tokensmith.repository.repo.GivenNameRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by tommackenzie on 5/16/17.
 */
public class GivenNameRepoTest {

    @Mock
    private GivenNameMapper mockGivenNameMapper;
    private GivenNameRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new GivenNameRepo(mockGivenNameMapper);
    }

    @Test
    public void testInsertShouldBeOk() {
        Name givenName = new Name();
        subject.insert(givenName);

        verify(mockGivenNameMapper, times(1)).insert(givenName);
    }

    @Test
    public void updateShouldBeOk() {
        UUID resourceOwnerId = UUID.randomUUID();
        Name givenName = new Name();

        subject.update(resourceOwnerId, givenName);

        verify(mockGivenNameMapper).update(resourceOwnerId, givenName);
    }

    @Test
    public void deleteShouldBeOk() {
        UUID resourceOwnerId = UUID.randomUUID();
        Name givenName = new Name();

        subject.delete(resourceOwnerId, givenName);

        verify(mockGivenNameMapper).delete(resourceOwnerId, givenName);
    }
}