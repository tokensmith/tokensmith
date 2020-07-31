package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.mapper.HealthMapper;
import net.tokensmith.repository.repo.HealthRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class HealthRepoTest {
    @Mock
    private HealthMapper mockHealthMapper;
    private HealthRepository subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new HealthRepo(mockHealthMapper);
    }

    @Test
    public void okShouldBeTrue() {
        Integer ok = 1;
        when(mockHealthMapper.ok()).thenReturn(ok);

        Boolean actual = subject.isOk();
        assertTrue(actual);
    }

    @Test
    public void okWhenNullShouldBeFalse() {
        Integer ok = null;
        when(mockHealthMapper.ok()).thenReturn(ok);

        Boolean actual = subject.isOk();
        assertFalse(actual);
    }

    @Test
    public void okWhenTwoShouldBeFalse() {
        Integer ok = 2;
        when(mockHealthMapper.ok()).thenReturn(ok);

        Boolean actual = subject.isOk();
        assertFalse(actual);
    }

}