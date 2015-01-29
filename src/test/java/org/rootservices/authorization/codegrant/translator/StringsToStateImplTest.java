package org.rootservices.authorization.codegrant.translator;

import org.junit.Before;
import org.junit.Test;
import org.rootservices.authorization.codegrant.translator.exception.ValidationError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.fest.assertions.api.Assertions.assertThat;


public class StringsToStateImplTest {

    private StringsToStateImpl subject;

    @Before
    public void run() {
        subject = new StringsToStateImpl();
    }

    @Test
    public void runIsOk() throws ValidationError {
        String expected = "some-state";
        List<String> items = new ArrayList<>();
        String item = expected;
        items.add(item);

        String actual = subject.run(items);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void runEmptyList() throws ValidationError {
        List<String> items = new ArrayList<>();

        String actual = subject.run(items);
        assertThat(actual).isNull();
    }

    @Test(expected=ValidationError.class)
    public void runHasTooManyItems() throws ValidationError {
        List<String> items = new ArrayList<>();
        String item = "items";
        items.add(item);
        items.add(item);

        subject.run(items);
    }

    @Test(expected=ValidationError.class)
    public void runIsEmpty() throws ValidationError {
        List<String> items = new ArrayList<>();
        String item = "";
        items.add(item);

        subject.run(items);
    }
}