package org.rootservices.authorization.grant.code.factory.optional;

import org.apache.commons.validator.routines.UrlValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.grant.code.constant.ErrorCode;
import org.rootservices.authorization.grant.code.factory.exception.RedirectUriException;
import org.rootservices.authorization.grant.code.validator.OptionalParam;
import org.rootservices.authorization.grant.code.validator.exception.EmptyValueError;
import org.rootservices.authorization.grant.code.validator.exception.MoreThanOneItemError;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 2/1/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class RedirectUriFactoryImplTest {

    @Mock
    private OptionalParam mockOptionalParam;

    @Mock
    private UrlValidator mockUrlValidator;

    private RedirectUriFactory subject;

    @Before
    public void setUp() {
        subject = new RedirectUriFactoryImpl(mockOptionalParam, mockUrlValidator);
    }

    @Test
    public void testMakeRedirectUri() throws MoreThanOneItemError, EmptyValueError, RedirectUriException, URISyntaxException {

        String expectedUriValue = "https://rootservices.org";
        Optional<URI> expected = Optional.ofNullable(new URI(expectedUriValue));

        List<String> items = new ArrayList<>();
        items.add(expectedUriValue);

        when(mockOptionalParam.run(items)).thenReturn(true);
        when(mockUrlValidator.isValid(expectedUriValue)).thenReturn(true);

        Optional<URI> actual = subject.makeRedirectUri(items);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testMakeRedirectUriWhenItemsAreNull() throws MoreThanOneItemError, EmptyValueError, RedirectUriException {
        Optional<URI> expected = Optional.empty();
        List<String> items = null;

        when(mockOptionalParam.run(items)).thenReturn(true);

        Optional<URI> actual = subject.makeRedirectUri(items);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testMakeRedirectUriWhenEmptyList() throws MoreThanOneItemError, EmptyValueError, RedirectUriException {
        Optional<URI> expected = Optional.empty();
        List<String> items = new ArrayList<>();

        when(mockOptionalParam.run(items)).thenReturn(true);

        Optional<URI> actual = subject.makeRedirectUri(items);
        assertThat(actual).isEqualTo(expected);
    }


    public void testMakeRedirectUriWhenItemIsNotUri() throws MoreThanOneItemError, EmptyValueError, RedirectUriException, URISyntaxException {

        List<String> items = new ArrayList<>();
        items.add("not-a-uri");

        when(mockOptionalParam.run(items)).thenReturn(true);
        when(mockUrlValidator.isValid(items.get(0))).thenReturn(false);

        try {
            subject.makeRedirectUri(items);
            fail("RedirectUriException was expected.");
        } catch (RedirectUriException e) {
            assertThat(e.getDomainCause()).isNull();
            assertThat(e.getCode()).isEqualTo(ErrorCode.REDIRECT_URI_DATA_TYPE.getCode());
        }
    }

    @Test
    public void testMakeRedirectUriEmptyValueError() throws MoreThanOneItemError, EmptyValueError, RedirectUriException, URISyntaxException {

        List<String> items = new ArrayList<>();
        items.add("");

        when(mockOptionalParam.run(items)).thenThrow(EmptyValueError.class);

        try {
            subject.makeRedirectUri(items);
            fail("RedirectUriException was expected.");
        } catch (RedirectUriException e) {
            assertThat(e.getDomainCause() instanceof EmptyValueError).isEqualTo(true);
            assertThat(e.getCode()).isEqualTo(ErrorCode.REDIRECT_URI_EMPTY_VALUE.getCode());
        }
    }

    @Test
    public void testMakeRedirectUriMoreThanOneItemError() throws MoreThanOneItemError, EmptyValueError, RedirectUriException, URISyntaxException {

        List<String> items = new ArrayList<>();
        items.add("https://rootservices.org");
        items.add("https://rootservices.org");

        when(mockOptionalParam.run(items)).thenThrow(MoreThanOneItemError.class);

        try {
            subject.makeRedirectUri(items);
            fail("RedirectUriException was expected.");
        } catch (RedirectUriException e) {
            assertThat(e.getDomainCause() instanceof MoreThanOneItemError).isEqualTo(true);
            assertThat(e.getCode()).isEqualTo(ErrorCode.REDIRECT_URI_MORE_THAN_ONE_ITEM.getCode());
        }
    }

}

