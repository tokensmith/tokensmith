package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.repository.repo.FamilyNameRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.repository.entity.FamilyName;
import net.tokensmith.authorization.persistence.mapper.FamilyNameMapper;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class FamilyNameRepositoryImplTest {
    @Mock
    private FamilyNameMapper mockFamilyNameMapper;
    private FamilyNameRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new FamilyNameRepositoryImpl(mockFamilyNameMapper);
    }

    @Test
    public void testInsertShouldBeOk() {
        FamilyName familyName = new FamilyName();
        subject.insert(familyName);

        verify(mockFamilyNameMapper, times(1)).insert(familyName);
    }
}