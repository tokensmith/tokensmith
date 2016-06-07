package org.rootservices.authorization.oauth2.grant.redirect.authorization.request.builder.optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.exception.StateException;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.optional.StateBuilder;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.optional.StateBuilderImpl;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.validator.OptionalParam;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.validator.exception.EmptyValueError;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.validator.exception.MoreThanOneItemError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 2/1/15.
 */
public class StateBuilderImplTest {

    @Mock
    private OptionalParam mockOptionalParam;

    private StateBuilder subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new StateBuilderImpl(mockOptionalParam);
    }

    @Test
    public void testMakeState() throws MoreThanOneItemError, EmptyValueError, StateException {
        String expectedValue = "state";
        Optional<String> expected = Optional.ofNullable(expectedValue);

        List<String> items = new ArrayList<>();
        items.add(expectedValue);

        when(mockOptionalParam.run(items)).thenReturn(true);
        Optional<String> actual = subject.makeState(items);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testMakeStateWhenStatesAreNull() throws MoreThanOneItemError, EmptyValueError, StateException {
        Optional<String> expected = Optional.ofNullable(null);

        List<String> items = null;

        when(mockOptionalParam.run(items)).thenReturn(true);
        Optional<String> actual = subject.makeState(items);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testMakeStateEmptyList() throws MoreThanOneItemError, EmptyValueError, StateException {
        Optional<String> expected = Optional.empty();

        List<String> items = new ArrayList<>();

        when(mockOptionalParam.run(items)).thenReturn(true);
        Optional<String> actual = subject.makeState(items);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testMakeScopesEmptyValueError() throws MoreThanOneItemError, EmptyValueError {

        List<String> items = new ArrayList<>();
        items.add("");

        when(mockOptionalParam.run(items)).thenThrow(EmptyValueError.class);

        try {
            subject.makeState(items);
            fail("StateException was expected.");
        } catch (StateException e) {
            assertThat(e.getDomainCause() instanceof EmptyValueError).isEqualTo(true);
            assertThat(e.getCode()).isEqualTo(ErrorCode.STATE_EMPTY_VALUE.getCode());
        }
    }

    @Test
    public void testMakeScopesMoreThanOneItemError() throws MoreThanOneItemError, EmptyValueError {

        List<String> items = new ArrayList<>();
        items.add("Scope1");
        items.add("Scope2");

        when(mockOptionalParam.run(items)).thenThrow(MoreThanOneItemError.class);

        try {
            subject.makeState(items);
            fail("StateException was expected.");
        } catch (StateException e) {
            assertThat(e.getDomainCause() instanceof MoreThanOneItemError).isEqualTo(true);
            assertThat(e.getCode()).isEqualTo(ErrorCode.STATE_MORE_THAN_ONE_ITEM.getCode());
        }
    }
}
