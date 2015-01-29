package org.rootservices.authorization.codegrant.translator;

import org.junit.Before;
import org.junit.Test;
import org.rootservices.authorization.codegrant.translator.exception.EmptyValueError;
import org.rootservices.authorization.codegrant.translator.exception.InvalidValueError;
import org.rootservices.authorization.codegrant.translator.exception.ValidationError;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class StringsToURIImplTest {

    StringsToURI subject;

    @Before
    public void run() {
        subject = new StringsToURIImpl();
    }

    @Test
    public void hasZeroItems() throws ValidationError {
        List<String> items = new ArrayList<>();
        URI expected = null;

        URI actual = subject.run(items);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void isOkOneScope() throws ValidationError, URISyntaxException {
        String redirectString = "https://rootservices.org";

        List<String> items = new ArrayList<>();
        items.add(redirectString);

        URI expected = new URI(redirectString);

        URI actual = subject.run(items);
        assertThat(actual).isEqualTo(expected);
    }

    @Test(expected=ValidationError.class)
    public void runHasTooManyItems() throws ValidationError {
        String redirectString = "https://rootservices.org";
        List<String> items = new ArrayList<>();
        items.add(redirectString);
        items.add(redirectString);

        subject.run(items);
    }

    @Test(expected=EmptyValueError.class)
    public void runOneItemEmptyValue() throws ValidationError {
        List<String> items = new ArrayList<>();
        items.add("");

        subject.run(items);
    }
}