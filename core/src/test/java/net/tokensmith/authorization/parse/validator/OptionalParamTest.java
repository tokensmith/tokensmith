package net.tokensmith.authorization.parse.validator;


import org.junit.Before;
import org.junit.Test;
import net.tokensmith.authorization.parse.validator.excpeption.EmptyValueError;
import net.tokensmith.authorization.parse.validator.excpeption.MoreThanOneItemError;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


public class OptionalParamTest {

    private OptionalParam subject;

    @Before
    public void setUp() {
        subject = new OptionalParam();
    }

    private List<String> makeItems() {
        List<String> items = new ArrayList<>();
        items.add("item1");
        return items;
    }

    @Test
    public void runWhenOneItemShouldBeOK() throws Exception {
        List<String> items = makeItems();

        boolean actual = subject.run(items);
        assertThat(actual, is(true));
    }

    @Test
    public void runWhenZeroItemsShouldBeOK() throws Exception {
        List<String> items = new ArrayList<>();

        boolean actual = subject.run(items);
        assertThat(actual, is(true));
    }

    @Test(expected=MoreThanOneItemError.class)
    public void runWhenTooManyItemsShouldThrowMoreThanOneItemError() throws Exception {
        List<String> items = makeItems();
        items.add("item2");

        subject.run(items);
    }

    @Test(expected=EmptyValueError.class)
    public void runWhenOneItemIsEmptyShouldThrowEmptyValueError() throws Exception {
        List<String> items = new ArrayList<>();
        items.add("");

        subject.run(items);
    }

}