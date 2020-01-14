package net.toknsmith.login.exception.builder;

import helper.Factory;
import net.toknsmith.login.endpoint.entity.response.openid.OpenIdToken;
import net.toknsmith.login.endpoint.entity.response.openid.claim.User;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class IdTokenExceptionBuilderTest {

    @Test
    public void fromTokenEndpointShouldHaveTokens() {
        User user = new User();
        Throwable cause = new RuntimeException();

        OpenIdToken openIdToken = Factory.okIdToken();
        var actual = new IdTokenExceptionBuilder()
                .message("foo")
                .user(user)
                .cause(cause)
                .fromTokenEndpoint(openIdToken)
                .build();

        assertThat(actual.getMessage(), is("foo"));
        assertThat(actual.getUser(), is(user));
        assertThat(actual.getCause(), is(cause));

        assertThat(actual.getAccessToken(), is(notNullValue()));
        assertThat(actual.getAccessToken().isPresent(), is(true));
        assertThat(actual.getAccessToken().get(), is(openIdToken.getAccessToken()));

        assertThat(actual.getRefreshToken(), is(notNullValue()));
        assertThat(actual.getRefreshToken().isPresent(), is(true));
        assertThat(actual.getRefreshToken().get(), is(openIdToken.getRefreshToken()));

        assertThat(actual.getExpiresIn(), is(notNullValue()));
        assertThat(actual.getExpiresIn().isPresent(), is(true));
        assertThat(actual.getExpiresIn().get(), is(openIdToken.getExpiresIn()));

        assertThat(actual.getTokenType(), is(notNullValue()));
        assertThat(actual.getTokenType().isPresent(), is(true));
        assertThat(actual.getTokenType().get(), is(openIdToken.getTokenType()));
    }

    @Test
    public void fromUserInfoShouldHaveTokens() {

        User user = new User();
        Throwable cause = new RuntimeException();

        var actual = new IdTokenExceptionBuilder()
                .message("foo")
                .user(user)
                .cause(cause)
                .build();

        assertThat(actual.getMessage(), is("foo"));
        assertThat(actual.getUser(), is(user));
        assertThat(actual.getCause(), is(cause));

        assertThat(actual.getAccessToken(), is(notNullValue()));
        assertThat(actual.getAccessToken().isPresent(), is(false));

        assertThat(actual.getRefreshToken(), is(notNullValue()));
        assertThat(actual.getRefreshToken().isPresent(), is(false));

        assertThat(actual.getExpiresIn(), is(notNullValue()));
        assertThat(actual.getExpiresIn().isPresent(), is(false));

        assertThat(actual.getTokenType(), is(notNullValue()));
        assertThat(actual.getTokenType().isPresent(), is(false));
    }
}
