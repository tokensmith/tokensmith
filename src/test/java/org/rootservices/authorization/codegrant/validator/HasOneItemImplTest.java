package org.rootservices.authorization.codegrant.validator;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;


public class HasOneItemImplTest {

    private HasOneItem subject;

    @Before
    public void setUp() {
        subject = new HasOneItemImpl();
    }

    @Test
    public void runIsNull() {
        List<String> items = null;

        boolean actual = subject.run(items);
        assertThat(actual).isEqualTo(false);
    }

    @Test
    public void runEmptyList() {
        List<String> items = new ArrayList<>();

        boolean actual = subject.run(items);
        assertThat(actual).isEqualTo(false);
    }

    @Test
    public void runTooManyItems() {
        List<String> items = new ArrayList<>();
        String item = "item";
        items.add(item);
        items.add(item);

        boolean actual = subject.run(items);
        assertThat(actual).isEqualTo(false);
    }

    @Test
    public void runHasOneItem() {
        List<String> items = new ArrayList<>();
        String item = "item";
        items.add(item);

        boolean actual = subject.run(items);
        assertThat(actual).isEqualTo(true);
    }
}