package org.rootservices.authorization.http.factory;

import helpers.category.UnitTests;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.rootservices.authorization.oauth2.grant.token.entity.Extension;
import org.rootservices.authorization.openId.identity.MakeCodeGrantIdentityToken;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 2/20/16.
 */
@Category(UnitTests.class)
public class MakeTokenFactoryTest {

    private MakeTokenFactory subject;
    @Mock
    private MakeCodeGrantIdentityToken mockBuildIdentityToken;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new MakeTokenFactory(mockBuildIdentityToken);
    }

    @Test
    public void makeShouldReturnMakeOpendIdToken() {
        MakeToken actual = subject.make(Extension.IDENTITY);
        assertThat(actual, instanceOf(MakeOpenIdToken.class));
    }

    @Test
    public void makeShouldReturnMakeOAuthToken() {
        MakeToken actual = subject.make(Extension.NONE);
        assertThat(actual, instanceOf(MakeOAuthToken.class));
    }

}