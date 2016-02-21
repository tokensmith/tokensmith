package org.rootservices.authorization.grant.openid.protocol.authorization.request.builder.required;

import org.apache.commons.validator.routines.UrlValidator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.exception.RedirectUriException;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.validator.RequiredParam;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.validator.exception.EmptyValueError;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.validator.exception.MoreThanOneItemError;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.validator.exception.NoItemsError;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.validator.exception.ParamIsNullError;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 10/2/15.
 */
public class OpenIdRedirectUriBuilderImplTest {

    @Mock
    private RequiredParam mockRequiredParam;
    @Mock
    private UrlValidator mockUrlValidator;

    private OpenIdRedirectUriBuilder subject;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new OpenIdRedirectUriBuilderImpl(mockRequiredParam, mockUrlValidator);
    }

    @Test
    public void validUriShouldBuild() throws NoItemsError, ParamIsNullError, MoreThanOneItemError, EmptyValueError, RedirectUriException {

        List<String> items = new ArrayList<>();
        items.add("https://rootservices.org");

        when(mockRequiredParam.run(items)).thenReturn(true);
        when(mockUrlValidator.isValid(items.get(0))).thenReturn(true);

        URI actual = subject.build(items);
        assertThat(actual.toString()).isEqualTo(items.get(0));
    }

    @Test
    public void emptyValueShouldThowRedirectUriException() throws NoItemsError, ParamIsNullError, MoreThanOneItemError, EmptyValueError {

        List<String> items = new ArrayList<>();
        items.add("");

        when(mockRequiredParam.run(items)).thenThrow(EmptyValueError.class);

        try {
            subject.build(items);
            fail("RedirectUriException was expected.");
        } catch (RedirectUriException e) {
            assertThat(e.getDomainCause() instanceof EmptyValueError).isEqualTo(true);
            assertThat(e.getCode()).isEqualTo(ErrorCode.REDIRECT_URI_EMPTY_VALUE.getCode());
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
            subject.build(items);
            fail("RedirectUriException was expected.");
        } catch (RedirectUriException e) {
            assertThat(e.getDomainCause() instanceof MoreThanOneItemError).isEqualTo(true);
            assertThat(e.getCode()).isEqualTo(ErrorCode.REDIRECT_URI_MORE_THAN_ONE_ITEM.getCode());
        }
    }

    @Test
    public void noItemsShouldThrowRedirectUriException() throws NoItemsError, ParamIsNullError, MoreThanOneItemError, EmptyValueError {

        List<String> items = new ArrayList<>();

        when(mockRequiredParam.run(items)).thenThrow(NoItemsError.class);

        try {
            subject.build(items);
            fail("RedirectUriException was expected.");
        } catch (RedirectUriException e) {
            assertThat(e.getDomainCause() instanceof NoItemsError).isEqualTo(true);
            assertThat(e.getCode()).isEqualTo(ErrorCode.REDIRECT_URI_EMPTY_LIST.getCode());
        }
    }

    @Test
    public void nullShouldThrowRedirectUriException() throws NoItemsError, ParamIsNullError, MoreThanOneItemError, EmptyValueError {

        List<String> items = null;

        when(mockRequiredParam.run(items)).thenThrow(ParamIsNullError.class);

        try {
            subject.build(items);
            fail("RedirectUriException was expected.");
        } catch (RedirectUriException e) {
            assertThat(e.getDomainCause() instanceof ParamIsNullError).isEqualTo(true);
            assertThat(e.getCode()).isEqualTo(ErrorCode.REDIRECT_URI_NULL.getCode());
        }
    }

    @Test
    public void invalidUriShouldThrowRedirectUriException() throws NoItemsError, ParamIsNullError, MoreThanOneItemError, EmptyValueError {

        List<String> items = new ArrayList<>();
        items.add("invalid-redirect-uri");

        when(mockRequiredParam.run(items)).thenReturn(true);
        when(mockUrlValidator.isValid(items.get(0))).thenReturn(false);

        try {
            subject.build(items);
            fail("RedirectUriException was expected.");
        } catch (RedirectUriException e) {
            assertThat(e.getCode()).isEqualTo(ErrorCode.REDIRECT_URI_DATA_TYPE.getCode());
        }
    }

}