package org.rootservices.authorization.codegrant.factory.optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.codegrant.factory.exception.DataTypeException;
import org.rootservices.authorization.codegrant.validator.OptionalParam;
import org.rootservices.authorization.codegrant.validator.exception.EmptyValueError;
import org.rootservices.authorization.codegrant.validator.exception.MoreThanOneItemError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 2/1/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class StateFactoryImplTest {

    @Mock
    private OptionalParam mockOptionalParam;

    private StateFactory subject;

    @Before
    public void setUp() {
        subject = new StateFactoryImpl(mockOptionalParam);
    }

    @Test
    public void testMakeState() throws MoreThanOneItemError, EmptyValueError, DataTypeException {
        String expectedValue = "state";
        Optional<String> expected = Optional.ofNullable(expectedValue);

        List<String> items = new ArrayList<>();
        items.add(expectedValue);

        when(mockOptionalParam.run(items)).thenReturn(true);
        Optional<String> actual = subject.makeState(items);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testMakeStateWhenStatesAreNull() throws MoreThanOneItemError, EmptyValueError, DataTypeException {
        Optional<String> expected = Optional.ofNullable(null);

        List<String> items = null;

        when(mockOptionalParam.run(items)).thenReturn(true);
        Optional<String> actual = subject.makeState(items);
        assertThat(actual).isEqualTo(expected);
    }
}
