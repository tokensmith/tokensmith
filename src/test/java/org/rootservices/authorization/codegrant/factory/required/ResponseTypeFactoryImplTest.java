package org.rootservices.authorization.codegrant.factory.required;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.codegrant.factory.exception.DataTypeException;
import org.rootservices.authorization.codegrant.validator.RequiredParam;
import org.rootservices.authorization.codegrant.validator.exception.EmptyValueError;
import org.rootservices.authorization.codegrant.validator.exception.MoreThanOneItemError;
import org.rootservices.authorization.codegrant.validator.exception.NoItemsError;
import org.rootservices.authorization.codegrant.validator.exception.ParamIsNullError;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 2/1/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class ResponseTypeFactoryImplTest {

    @Mock
    private RequiredParam mockRequiredParam;

    private ResponseTypeFactory subject;

    @Before
    public void setUp() {
        subject = new ResponseTypeFactoryImpl(mockRequiredParam);
    }

    @Test
    public void testMakeResponseType() throws NoItemsError, ParamIsNullError, MoreThanOneItemError, EmptyValueError, DataTypeException {
        ResponseType expected = ResponseType.CODE;

        List<String> items = new ArrayList<>();
        items.add(expected.toString());

        when(mockRequiredParam.run(items)).thenReturn(true);

        ResponseType actual = subject.makeResponseType(items);
        assertThat(actual).isEqualTo(expected);
    }

    @Test(expected = DataTypeException.class)
    public void testMakeResponseTypeIsNotResponseType() throws NoItemsError, ParamIsNullError, MoreThanOneItemError, EmptyValueError, DataTypeException {
        List<String> items = new ArrayList<>();
        items.add("Unknown Response Type");

        when(mockRequiredParam.run(items)).thenReturn(true);

        ResponseType actual = subject.makeResponseType(items);
    }
}
