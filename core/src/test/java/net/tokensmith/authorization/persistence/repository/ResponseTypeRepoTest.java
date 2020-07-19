package net.tokensmith.authorization.persistence.repository;

import helper.fixture.FixtureFactory;
import net.tokensmith.authorization.persistence.mapper.ResponseTypeMapper;
import net.tokensmith.repository.entity.ResponseType;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.ResponseTypeRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 8/9/16.
 */
public class ResponseTypeRepoTest {
    @Mock
    private ResponseTypeMapper mockResponseTypeMapper;
    private ResponseTypeRepository subject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new ResponseTypeRepo(mockResponseTypeMapper);
    }

    @Test
    public void getByNameShouldBeOk() throws Exception {
        ResponseType responseType = FixtureFactory.makeResponseType();
        when(mockResponseTypeMapper.getByName("CODE")).thenReturn(responseType);

        ResponseType actual = subject.getByName("CODE");

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(responseType));
    }

    @Test(expected = RecordNotFoundException.class)
    public void getByNameShouldThrowRecordNotFound() throws Exception {

        when(mockResponseTypeMapper.getByName("CODE")).thenReturn(null);

        subject.getByName("CODE");
    }
}