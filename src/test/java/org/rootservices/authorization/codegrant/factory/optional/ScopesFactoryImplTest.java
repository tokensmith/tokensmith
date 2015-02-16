package org.rootservices.authorization.codegrant.factory.optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.codegrant.constant.ErrorCode;
import org.rootservices.authorization.codegrant.factory.exception.ScopesException;
import org.rootservices.authorization.codegrant.validator.OptionalParam;
import org.rootservices.authorization.codegrant.validator.exception.EmptyValueError;
import org.rootservices.authorization.codegrant.validator.exception.MoreThanOneItemError;
import org.rootservices.authorization.persistence.entity.Scope;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 2/1/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class ScopesFactoryImplTest {

    @Mock
    private OptionalParam mockOptionalParam;

    private ScopesFactory subject;

    @Before
    public void setUp() {
        subject = new ScopesFactoryImpl(mockOptionalParam);
    }

    @Test
    public void testMakeScopes() throws MoreThanOneItemError, EmptyValueError, ScopesException {
        List<Scope> expected = new ArrayList<>();
        expected.add(Scope.PROFILE);

        List<String> items = new ArrayList<>();
        items.add(expected.get(0).toString());

        when(mockOptionalParam.run(items)).thenReturn(true);

        List<Scope> actual = subject.makeScopes(items);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testMakeScopesWhenScopesAreNull() throws MoreThanOneItemError, EmptyValueError, ScopesException {
        List<Scope> expected = null;
        List<String> items = null;

        when(mockOptionalParam.run(items)).thenReturn(true);

        List<Scope> actual = subject.makeScopes(items);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testMakeScopesWhenScopesAreEmptyList() throws MoreThanOneItemError, EmptyValueError, ScopesException {
        List<Scope> expected = null;
        List<String> items = new ArrayList<>();

        when(mockOptionalParam.run(items)).thenReturn(true);

        List<Scope> actual = subject.makeScopes(items);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testMakeScopesEmptyValueError() throws MoreThanOneItemError, EmptyValueError {

        List<String> items = new ArrayList<>();
        items.add("");

        when(mockOptionalParam.run(items)).thenThrow(EmptyValueError.class);

        try {
            subject.makeScopes(items);
            fail("ScopesException was expected.");
        } catch (ScopesException e) {
            assertThat(e.getDomainCause() instanceof EmptyValueError).isEqualTo(true);
            assertThat(e.getCode()).isEqualTo(ErrorCode.SCOPES_EMPTY_VALUE.getCode());
        }

    }

    @Test
    public void testMakeScopesMoreThanOneItemError() throws MoreThanOneItemError, EmptyValueError {

        List<String> items = new ArrayList<>();
        items.add(Scope.PROFILE.toString());
        items.add(Scope.PROFILE.toString());

        when(mockOptionalParam.run(items)).thenThrow(MoreThanOneItemError.class);

        try {
            subject.makeScopes(items);
            fail("ScopesException was expected.");
        } catch (ScopesException e) {
            assertThat(e.getDomainCause() instanceof MoreThanOneItemError).isEqualTo(true);
            assertThat(e.getCode()).isEqualTo(ErrorCode.SCOPES_MORE_THAN_ONE_ITEM.getCode());
        }

    }


    @Test
    public void testMakeScopesDataTypeError() throws MoreThanOneItemError, EmptyValueError {

        List<String> items = new ArrayList<>();
        items.add("invalid");

        when(mockOptionalParam.run(items)).thenReturn(true);

        try {
            subject.makeScopes(items);
            fail("ScopesException was expected.");
        } catch (ScopesException e) {
            assertThat(e.getDomainCause() instanceof IllegalArgumentException).isEqualTo(true);
            assertThat(e.getCode()).isEqualTo(ErrorCode.SCOPES_DATA_TYPE.getCode());
        }

    }
}
