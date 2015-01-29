package org.rootservices.authorization.codegrant.validator;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class IsNotNullImplTest {

    IsNotNull subject;

    @Before
    public void setUp() {
        subject = new IsNotNullImpl();
    }

    @Test
    public void runStringIsNull() {
        String item = null;
        boolean actual = subject.run(item);

        assertThat(actual).isEqualTo(false);
    }

    @Test
    public void runStringIsNotNull() {
        String item = "item";
        boolean actual = subject.run(item);

        assertThat(actual).isEqualTo(true);
    }
}