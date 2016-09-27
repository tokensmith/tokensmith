package org.rootservices.authorization.oauth2.grant.foo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.oauth2.grant.foo.factory.RequestTokenFactory;
import org.rootservices.authorization.oauth2.grant.password.RequestTokenPasswordGrant;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 9/21/16.
 */
public class RequestTokenFactoryTest {
    @Mock
    private RequestTokenPasswordGrant mockRequestTokenPasswordGrant;

    private RequestTokenFactory subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new RequestTokenFactory(mockRequestTokenPasswordGrant);
    }

    @Test
    public void makeShouldBePassword() throws Exception {
        RequestTokenGrant actual = subject.make("password");
        assertThat(actual, instanceOf(RequestTokenPasswordGrant.class));
    }

    @Test
    public void makeWontThrowNPE() {
        RequestTokenGrant actual = subject.make(null);
        assertThat(actual, is(nullValue()));
    }
}