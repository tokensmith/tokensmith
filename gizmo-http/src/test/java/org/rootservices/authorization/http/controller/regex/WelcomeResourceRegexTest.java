package org.rootservices.authorization.http.controller.regex;

import helpers.category.UnitTests;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.rootservices.authorization.http.controller.resource.html.authorization.welcome.WelcomeResource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@Category(UnitTests.class)
public class WelcomeResourceRegexTest {

    private WelcomeResource subject;

    @Before
    public void setUp() {
        subject = new WelcomeResource();
    }

    @Test
    public void urlShouldMatch() {
        Pattern pattern = Pattern.compile(subject.URL);
        Matcher matcher = pattern.matcher("/welcome?nonce=foo");
        assertThat(matcher.matches(), is(true));
    }
}
