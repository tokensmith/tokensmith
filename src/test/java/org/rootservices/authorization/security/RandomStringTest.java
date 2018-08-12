package org.rootservices.authorization.security;

import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 11/5/16.
 */
public class RandomStringTest {
    private RandomString subject;

    @Before
    public void setUp() {
        subject = new RandomString();
    }

    @Test
    public void runNumChars32ShouldReturnCorrectLength() {
        String actual = subject.run(32);
        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void runNumChars64ShouldReturnCorrectLength() {
        String actual = subject.run(64);
        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void runNumChars128ShouldReturnCorrectLength() throws UnsupportedEncodingException {
        String actual = subject.run(128);
        assertThat(actual, is(notNullValue()));
    }
}