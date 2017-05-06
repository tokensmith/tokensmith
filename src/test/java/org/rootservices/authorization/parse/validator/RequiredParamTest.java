package org.rootservices.authorization.parse.validator;


import org.junit.Before;
import org.junit.Test;
import org.rootservices.authorization.parse.validator.excpeption.EmptyValueError;
import org.rootservices.authorization.parse.validator.excpeption.MoreThanOneItemError;
import org.rootservices.authorization.parse.validator.excpeption.NoItemsError;
import org.rootservices.authorization.parse.validator.excpeption.ParamIsNullError;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;



public class RequiredParamTest {
    private RequiredParam subject;

    @Before
    public void setUp() {
        subject = new RequiredParam();
    }

    private List<String> makeItems() {
        List<String> items = new ArrayList<>();
        items.add("item1");
        return items;
    }

    @Test
    public void runWithOneItemShouldBeOk() throws Exception {
        List<String> items = makeItems();

        boolean actual = subject.run(items);
        assertThat(actual, is(true));
    }

    @Test(expected=ParamIsNullError.class)
    public void runWhenIsNullShouldThrowParamIsNullError() throws Exception {
        List<String> items = null;

        subject.run(items);
    }

    @Test(expected=NoItemsError.class)
    public void runWhenNoItemsShouldThrowNoItemsError() throws Exception {
        List<String> items = new ArrayList<>();

        subject.run(items);
    }

    @Test(expected=EmptyValueError.class)
    public void runWhenOneItemAndEmptyShouldThrowEmptyValueError() throws Exception {
        List<String> items = new ArrayList<>();
        items.add("");

        subject.run(items);
    }

    @Test(expected=MoreThanOneItemError.class)
    public void runWhenTwoItemsShouldThrowMoreThanOneItemError() throws Exception {
        List<String> items = makeItems();
        items.add("item2");

        subject.run(items);
    }

}