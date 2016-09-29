package org.rootservices.authorization.oauth2.grant.token;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.RequestTokenCodeGrant;
import org.rootservices.authorization.oauth2.grant.token.factory.RequestTokenGrantFactory;
import org.rootservices.authorization.oauth2.grant.password.RequestTokenPasswordGrant;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 9/21/16.
 */
public class RequestTokenGrantFactoryTest {
    @Mock
    private RequestTokenPasswordGrant mockRequestTokenPasswordGrant;
    @Mock
    private RequestTokenCodeGrant mockRequestTokenCodeGrant;

    private RequestTokenGrantFactory subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new RequestTokenGrantFactory(mockRequestTokenPasswordGrant, mockRequestTokenCodeGrant);
    }

    @Test
    public void makeShouldBePassword() throws Exception {
        RequestTokenGrant actual = subject.make("password");
        assertThat(actual, instanceOf(RequestTokenPasswordGrant.class));
    }

    @Test
    public void makeShouldBeCode() throws Exception {
        RequestTokenGrant actual = subject.make("authorization_code");
        assertThat(actual, instanceOf(RequestTokenCodeGrant.class));
    }

    @Test
    public void makeWontThrowNPE() {
        RequestTokenGrant actual = subject.make(null);
        assertThat(actual, is(nullValue()));
    }
}