package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.mapper.FamilyNameMapper;
import net.tokensmith.repository.entity.Name;
import net.tokensmith.repository.repo.FamilyNameRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class FamilyNameRepoTest {
    @Mock
    private FamilyNameMapper mockFamilyNameMapper;
    private FamilyNameRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new FamilyNameRepo(mockFamilyNameMapper);
    }

    @Test
    public void testInsertShouldBeOk() {
        Name name = new Name();
        subject.insert(name);

        verify(mockFamilyNameMapper, times(1)).insert(name);
    }

    @Test
    public void updateShouldBeOk() {
        UUID resourceOwnerId = UUID.randomUUID();
        Name name = new Name();

        subject.update(resourceOwnerId, name);

        verify(mockFamilyNameMapper).update(resourceOwnerId, name);
    }

    @Test
    public void deleteShouldBeOk() {
        UUID resourceOwnerId = UUID.randomUUID();
        Name name = new Name();

        subject.delete(resourceOwnerId, name);

        verify(mockFamilyNameMapper).delete(resourceOwnerId, name);
    }
}