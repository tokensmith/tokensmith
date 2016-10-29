package org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.factory.required;

import org.apache.commons.validator.routines.UrlValidator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.RedirectUriException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.RequiredParam;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.exception.EmptyValueError;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.exception.MoreThanOneItemError;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.exception.NoItemsError;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.exception.ParamIsNullError;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 10/2/15.
 */
public class OpenIdRedirectUriFactoryTest {

    @Mock
    private RequiredParam mockRequiredParam;
    @Mock
    private UrlValidator mockUrlValidator;

    private OpenIdRedirectUriFactory subject;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new OpenIdRedirectUriFactory(mockRequiredParam, mockUrlValidator);
    }

    @Test
    public void validUriThenShouldBeOk() throws NoItemsError, ParamIsNullError, MoreThanOneItemError, EmptyValueError, RedirectUriException {

        List<String> items = new ArrayList<>();
        items.add("https://rootservices.org");

        when(mockRequiredParam.run(items)).thenReturn(true);
        when(mockUrlValidator.isValid(items.get(0))).thenReturn(true);

        URI actual = subject.makeRedirectUri(items);
        assertThat(actual.toString(), is(items.get(0)));
    }

    @Test
    public void emptyValueShouldThrowRedirectUriException() throws NoItemsError, ParamIsNullError, MoreThanOneItemError, EmptyValueError {

        List<String> items = new ArrayList<>();
        items.add("");

        when(mockRequiredParam.run(items)).thenThrow(EmptyValueError.class);

        try {
            subject.makeRedirectUri(items);
            fail("RedirectUriException was expected.");
        } catch (RedirectUriException e) {
            assertThat(e.getCause(), instanceOf(EmptyValueError.class));
            assertThat(e.getCode(), is(ErrorCode.REDIRECT_URI_EMPTY_VALUE.getCode()));
        }
    }

    @Test
    public void moreThanOneItemShouldThrowRedirectUriException() throws NoItemsError, ParamIsNullError, MoreThanOneItemError, EmptyValueError {

        UUID uuid = UUID.randomUUID();
        List<String> items = new ArrayList<>();
        items.add(uuid.toString());
        items.add(uuid.toString());

        when(mockRequiredParam.run(items)).thenThrow(MoreThanOneItemError.class);

        try {
            subject.makeRedirectUri(items);
            fail("RedirectUriException was expected.");
        } catch (RedirectUriException e) {
            assertThat(e.getCause(), instanceOf(MoreThanOneItemError.class));
            assertThat(e.getCode(), is(ErrorCode.REDIRECT_URI_MORE_THAN_ONE_ITEM.getCode()));
        }
    }

    @Test
    public void noItemsShouldThrowRedirectUriException() throws NoItemsError, ParamIsNullError, MoreThanOneItemError, EmptyValueError {

        List<String> items = new ArrayList<>();

        when(mockRequiredParam.run(items)).thenThrow(NoItemsError.class);

        try {
            subject.makeRedirectUri(items);
            fail("RedirectUriException was expected.");
        } catch (RedirectUriException e) {
            assertThat(e.getCause(), instanceOf(NoItemsError.class));
            assertThat(e.getCode(), is(ErrorCode.REDIRECT_URI_EMPTY_LIST.getCode()));
        }
    }

    @Test
    public void nullShouldThrowRedirectUriException() throws NoItemsError, ParamIsNullError, MoreThanOneItemError, EmptyValueError {

        List<String> items = null;

        when(mockRequiredParam.run(items)).thenThrow(ParamIsNullError.class);

        try {
            subject.makeRedirectUri(items);
            fail("RedirectUriException was expected.");
        } catch (RedirectUriException e) {
            assertThat(e.getCause(), instanceOf(ParamIsNullError.class));
            assertThat(e.getCode(), is(ErrorCode.REDIRECT_URI_NULL.getCode()));
        }
    }

    @Test
    public void invalidUriShouldThrowRedirectUriException() throws NoItemsError, ParamIsNullError, MoreThanOneItemError, EmptyValueError {

        List<String> items = new ArrayList<>();
        items.add("invalid-redirect-uri");

        when(mockRequiredParam.run(items)).thenReturn(true);
        when(mockUrlValidator.isValid(items.get(0))).thenReturn(false);

        try {
            subject.makeRedirectUri(items);
            fail("RedirectUriException was expected.");
        } catch (RedirectUriException e) {
            assertThat(e.getCode(), is(ErrorCode.REDIRECT_URI_DATA_TYPE.getCode()));
        }
    }

}