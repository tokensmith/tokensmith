package org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator;

import org.junit.Before;
import org.junit.Test;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.exception.EmptyValueError;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.exception.MoreThanOneItemError;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


/**
 * Created by tommackenzie on 1/31/15.
 */
public class OptionalParamImplTest {

    private OptionalParam subject;

    @Before
    public void setUp() {
        subject = new OptionalParamImpl();
    }

    private List<String> makeItems() {
        List<String> items = new ArrayList<>();
        items.add("item1");
        return items;
    }

    @Test
    public void runHasOneItemIsOk() throws EmptyValueError, MoreThanOneItemError{
        List<String> items = makeItems();

        boolean actual = subject.run(items);
        assertThat(actual, is(true));
    }

    @Test
    public void runHasZeroItemsIsOk() throws EmptyValueError, MoreThanOneItemError{
        List<String> items = new ArrayList<>();

        boolean actual = subject.run(items);
        assertThat(actual, is(true));
    }

    @Test(expected=MoreThanOneItemError.class)
    public void runHasTooManyItems() throws EmptyValueError, MoreThanOneItemError {
        List<String> items = makeItems();
        items.add("item2");

        subject.run(items);
    }

    @Test(expected=EmptyValueError.class)
    public void runHasOneItemIsEmpty() throws EmptyValueError, MoreThanOneItemError {
        List<String> items = new ArrayList<>();
        items.add("");

        subject.run(items);
    }
}
