package org.rootservices.authorization.grant.code.protocol.authorization.request.builder.required;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.exception.ResponseTypeException;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.required.ResponseTypeBuilder;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.required.ResponseTypeBuilderImpl;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.validator.RequiredParam;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.validator.exception.EmptyValueError;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.validator.exception.MoreThanOneItemError;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.validator.exception.NoItemsError;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.validator.exception.ParamIsNullError;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 2/1/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class ResponseTypeBuilderImplTest {

    @Mock
    private RequiredParam mockRequiredParam;

    private ResponseTypeBuilder subject;

    @Before
    public void setUp() {
        subject = new ResponseTypeBuilderImpl(mockRequiredParam);
    }

    @Test
    public void testMakeResponseType() throws NoItemsError, ParamIsNullError, MoreThanOneItemError, EmptyValueError, ResponseTypeException{
        ResponseType expected = ResponseType.CODE;

        List<String> items = new ArrayList<>();
        items.add(expected.toString());

        when(mockRequiredParam.run(items)).thenReturn(true);

        ResponseType actual = subject.makeResponseType(items);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testMakeResponseTypeUnknownResponseType() throws NoItemsError, ParamIsNullError, MoreThanOneItemError, EmptyValueError {
        List<String> items = new ArrayList<>();
        items.add("Unknown Response Type");

        when(mockRequiredParam.run(items)).thenReturn(true);

        try {
            subject.makeResponseType(items);
            fail("ResponseTypeException was expected.");
        } catch (ResponseTypeException e) {
            assertThat(e.getDomainCause() instanceof IllegalArgumentException).isEqualTo(true);
            assertThat(e.getCode()).isEqualTo(ErrorCode.RESPONSE_TYPE_DATA_TYPE.getCode());
            assertThat(e.getError()).isEqualTo("unsupported_response_type");
        }
    }

    @Test
    public void testMakeResponseTypeEmptyValueError() throws NoItemsError, ParamIsNullError, MoreThanOneItemError, EmptyValueError {
        List<String> items = new ArrayList<>();
        items.add("");

        when(mockRequiredParam.run(items)).thenThrow(EmptyValueError.class);
        try {
            subject.makeResponseType(items);
            fail("ResponseTypeException was expected.");
        } catch (ResponseTypeException e) {
            assertThat(e.getDomainCause() instanceof EmptyValueError).isEqualTo(true);
            assertThat(e.getCode()).isEqualTo(ErrorCode.RESPONSE_TYPE_EMPTY_VALUE.getCode());
            assertThat(e.getError()).isEqualTo("invalid_request");
        }
    }

    @Test
    public void testMakeResponseTypeMoreThanOneItemError() throws NoItemsError, ParamIsNullError, MoreThanOneItemError, EmptyValueError {
        List<String> items = new ArrayList<>();
        items.add(ResponseType.CODE.toString());
        items.add(ResponseType.CODE.toString());

        when(mockRequiredParam.run(items)).thenThrow(MoreThanOneItemError.class);

        try {
            subject.makeResponseType(items);
            fail("ResponseTypeException was expected.");
        } catch (ResponseTypeException e) {
            assertThat(e.getDomainCause() instanceof MoreThanOneItemError).isEqualTo(true);
            assertThat(e.getCode()).isEqualTo(ErrorCode.RESPONSE_TYPE_MORE_THAN_ONE_ITEM.getCode());
            assertThat(e.getError()).isEqualTo("invalid_request");
        }
    }

    @Test
    public void testMakeResponseTypeNoItemsError() throws NoItemsError, ParamIsNullError, MoreThanOneItemError, EmptyValueError {
        List<String> items = new ArrayList<>();

        when(mockRequiredParam.run(items)).thenThrow(NoItemsError.class);

        try {
            subject.makeResponseType(items);
            fail("ResponseTypeException was expected.");
        } catch (ResponseTypeException e) {
            assertThat(e.getDomainCause() instanceof NoItemsError).isEqualTo(true);
            assertThat(e.getCode()).isEqualTo(ErrorCode.RESPONSE_TYPE_EMPTY_LIST.getCode());
            assertThat(e.getError()).isEqualTo("invalid_request");
        }
    }

    @Test
    public void testMakeResponseTypeParamIsNullError() throws NoItemsError, ParamIsNullError, MoreThanOneItemError, EmptyValueError {
        List<String> items = null;

        when(mockRequiredParam.run(items)).thenThrow(ParamIsNullError.class);
        try {
            subject.makeResponseType(items);
            fail("ResponseTypeException was expected.");
        } catch (ResponseTypeException e) {
            assertThat(e.getDomainCause() instanceof ParamIsNullError).isEqualTo(true);
            assertThat(e.getCode()).isEqualTo(ErrorCode.RESPONSE_TYPE_NULL.getCode());
            assertThat(e.getError()).isEqualTo("invalid_request");
        }
    }
}
