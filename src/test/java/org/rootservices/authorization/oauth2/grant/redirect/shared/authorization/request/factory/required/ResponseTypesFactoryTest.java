package org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.required;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.ResponseTypeException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.RequiredParam;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.exception.EmptyValueError;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.exception.MoreThanOneItemError;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.exception.NoItemsError;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.exception.ParamIsNullError;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 2/1/15.
 */
public class ResponseTypesFactoryTest {

    @Mock
    private RequiredParam mockRequiredParam;

    private ResponseTypesFactory subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new ResponseTypesFactory(mockRequiredParam);
    }

    @Test
    public void testMakeResponseType() throws NoItemsError, ParamIsNullError, MoreThanOneItemError, EmptyValueError, ResponseTypeException{
        String expected = "CODE";

        List<String> items = new ArrayList<>();
        items.add(expected);

        when(mockRequiredParam.run(items)).thenReturn(true);

        List<String> actual = subject.makeResponseTypes(items);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(1));
        assertThat(actual.get(0), is(expected));
    }

    @Test
    public void testMakeResponseTypeUnknownResponseType() throws NoItemsError, ParamIsNullError, MoreThanOneItemError, EmptyValueError {
        List<String> items = new ArrayList<>();
        items.add("Unknown-Response-Type");

        when(mockRequiredParam.run(items)).thenReturn(true);

        try {
            subject.makeResponseTypes(items);
            fail("ResponseTypeException was expected.");
        } catch (ResponseTypeException e) {
            assertThat(e.getDomainCause(), is(nullValue()));
            assertThat(e.getCode(), is(ErrorCode.RESPONSE_TYPE_DATA_TYPE.getCode()));
            assertThat(e.getError(), is("unsupported_response_type"));
        }
    }

    @Test
    public void testMakeResponseTypeEmptyValueError() throws NoItemsError, ParamIsNullError, MoreThanOneItemError, EmptyValueError {
        List<String> items = new ArrayList<>();
        items.add("");

        when(mockRequiredParam.run(items)).thenThrow(EmptyValueError.class);
        try {
            subject.makeResponseTypes(items);
            fail("ResponseTypeException was expected.");
        } catch (ResponseTypeException e) {
            assertThat(e.getDomainCause(), instanceOf(EmptyValueError.class));
            assertThat(e.getCode(), is(ErrorCode.RESPONSE_TYPE_EMPTY_VALUE.getCode()));
            assertThat(e.getError(), is("invalid_request"));
        }
    }

    @Test
    public void testMakeResponseTypeMoreThanOneItemError() throws NoItemsError, ParamIsNullError, MoreThanOneItemError, EmptyValueError {
        List<String> items = new ArrayList<>();
        items.add("CODE");
        items.add("CODE");

        when(mockRequiredParam.run(items)).thenThrow(MoreThanOneItemError.class);

        try {
            subject.makeResponseTypes(items);
            fail("ResponseTypeException was expected.");
        } catch (ResponseTypeException e) {
            assertThat(e.getDomainCause(), instanceOf(MoreThanOneItemError.class));
            assertThat(e.getCode(), is(ErrorCode.RESPONSE_TYPE_MORE_THAN_ONE_ITEM.getCode()));
            assertThat(e.getError(), is("invalid_request"));
        }
    }

    @Test
    public void testMakeResponseTypeNoItemsError() throws NoItemsError, ParamIsNullError, MoreThanOneItemError, EmptyValueError {
        List<String> items = new ArrayList<>();

        when(mockRequiredParam.run(items)).thenThrow(NoItemsError.class);

        try {
            subject.makeResponseTypes(items);
            fail("ResponseTypeException was expected.");
        } catch (ResponseTypeException e) {
            assertThat(e.getDomainCause(), instanceOf(NoItemsError.class));
            assertThat(e.getCode(), is(ErrorCode.RESPONSE_TYPE_EMPTY_LIST.getCode()));
            assertThat(e.getError(), is("invalid_request"));
        }
    }

    @Test
    public void testMakeResponseTypeParamIsNullError() throws NoItemsError, ParamIsNullError, MoreThanOneItemError, EmptyValueError {
        List<String> items = null;

        when(mockRequiredParam.run(items)).thenThrow(ParamIsNullError.class);
        try {
            subject.makeResponseTypes(items);
            fail("ResponseTypeException was expected.");
        } catch (ResponseTypeException e) {
            assertThat(e.getDomainCause(), instanceOf(ParamIsNullError.class));
            assertThat(e.getCode(), is(ErrorCode.RESPONSE_TYPE_NULL.getCode()));
            assertThat(e.getError(), is("invalid_request"));
        }
    }
}
