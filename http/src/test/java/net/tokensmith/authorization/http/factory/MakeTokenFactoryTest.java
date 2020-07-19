package net.tokensmith.authorization.http.factory;

import helpers.category.UnitTests;
import net.tokensmith.authorization.oauth2.grant.token.entity.Extension;
import net.tokensmith.authorization.openId.identity.MakeCodeGrantIdentityToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

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