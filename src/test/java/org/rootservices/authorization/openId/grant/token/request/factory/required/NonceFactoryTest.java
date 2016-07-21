package org.rootservices.authorization.openId.grant.token.request.factory.required;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.RequiredParam;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.exception.EmptyValueError;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.exception.MoreThanOneItemError;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.exception.NoItemsError;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.exception.ParamIsNullError;
import org.rootservices.authorization.openId.grant.token.request.factory.exception.NonceException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 7/21/16.
 */
public class NonceFactoryTest {

    @Mock
    private RequiredParam mockRequiredParam;

    private NonceFactory subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new NonceFactory(mockRequiredParam);
    }

    @Test
    public void makeNonceShouldBeOk() throws Exception {
        String expected = "nonce";
        List<String> items = new ArrayList<>();
        items.add(expected);

        when(mockRequiredParam.run(items)).thenReturn(true);

        String actual = subject.makeNonce(items);
        assertThat(actual, is(expected));
    }

    @Test
    public void testMakeNonceEmptyValueError() throws Exception {

        List<String> items = new ArrayList<>();
        items.add("");

        when(mockRequiredParam.run(items)).thenThrow(EmptyValueError.class);

        try {
            subject.makeNonce(items);
            fail("NonceException was expected.");
        } catch (NonceException e) {
            assertThat(e.getDomainCause(), instanceOf(EmptyValueError.class));
            assertThat(e.getCode(), is(ErrorCode.NONCE_EMPTY_VALUE.getCode()));
        }
    }

    @Test
    public void testMakeNonceMoreThanOneItemError() throws Exception {

        UUID uuid = UUID.randomUUID();
        List<String> items = new ArrayList<>();
        items.add(uuid.toString());
        items.add(uuid.toString());

        when(mockRequiredParam.run(items)).thenThrow(MoreThanOneItemError.class);

        try {
            subject.makeNonce(items);
            fail("NonceException was expected.");
        } catch (NonceException e) {
            assertThat(e.getDomainCause(), instanceOf(MoreThanOneItemError.class));
            assertThat(e.getCode(), is(ErrorCode.NONCE_MORE_THAN_ONE_ITEM.getCode()));
        }
    }

    @Test
    public void testMakeNonceNoItemsError() throws Exception {

        List<String> items = new ArrayList<>();

        when(mockRequiredParam.run(items)).thenThrow(NoItemsError.class);

        try {
            subject.makeNonce(items);
            fail("NonceException was expected.");
        } catch (NonceException e) {
            assertThat(e.getDomainCause(), instanceOf(NoItemsError.class));
            assertThat(e.getCode(), is(ErrorCode.NONCE_EMPTY_LIST.getCode()));
        }
    }

    @Test
    public void testMakeNonceParamIsNullError() throws Exception {

        List<String> items = null;

        when(mockRequiredParam.run(items)).thenThrow(ParamIsNullError.class);

        try {
            subject.makeNonce(items);
            fail("NonceException was expected.");
        } catch (NonceException e) {
            assertThat(e.getDomainCause(), instanceOf(ParamIsNullError.class));
            assertThat(e.getCode(), is(ErrorCode.NONCE_NULL.getCode()));
        }
    }
}