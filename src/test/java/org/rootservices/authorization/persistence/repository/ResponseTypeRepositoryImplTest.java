package org.rootservices.authorization.persistence.repository;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.ResponseTypeMapper;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 8/9/16.
 */
public class ResponseTypeRepositoryImplTest {
    @Mock
    private ResponseTypeMapper mockResponseTypeMapper;
    private ResponseTypeRepository subject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new ResponseTypeRepositoryImpl(mockResponseTypeMapper);
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