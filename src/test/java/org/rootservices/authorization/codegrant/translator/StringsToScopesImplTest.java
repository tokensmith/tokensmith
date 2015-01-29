package org.rootservices.authorization.codegrant.translator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.codegrant.translator.exception.EmptyValueError;
import org.rootservices.authorization.codegrant.translator.exception.InvalidValueError;
import org.rootservices.authorization.codegrant.translator.exception.ValidationError;
import org.rootservices.authorization.persistence.entity.Scope;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class StringsToScopesImplTest {

    StringsToScopes subject;

    @Before
    public void run() {
        subject = new StringsToScopesImpl();
    }

    @Test
    public void hasZeroItems() throws ValidationError {
        List<String> items = new ArrayList<>();
        List<Scope> expected = null;

        List<Scope> actual = subject.run(items);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void isOkOneScope() throws ValidationError {
        List<String> items = new ArrayList<>();
        items.add("profile");

        List<Scope> expected = new ArrayList<>();
        expected.add(Scope.PROFILE);

        List<Scope> actual = subject.run(items);
        assertThat(actual).isEqualTo(expected);
    }

    @Test(expected=ValidationError.class)
    public void runHasTooManyItems() throws ValidationError {
        List<String> items = new ArrayList<>();
        items.add("scope1");
        items.add("scope2");

        subject.run(items);
    }

    @Test(expected=EmptyValueError.class)
    public void runOneItemEmptyValue() throws ValidationError {
        List<String> items = new ArrayList<>();
        items.add("");

        subject.run(items);
    }

    @Test(expected=InvalidValueError.class)
    public void runInvalidScope() throws ValidationError {
        List<String> items = new ArrayList<>();
        items.add("InvalidScope");

        subject.run(items);
    }
}