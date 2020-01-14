package net.toknsmith.login.endpoint;


import net.toknsmith.login.HttpUtils;
import net.toknsmith.login.config.LoginFactory;
import net.toknsmith.login.config.props.EndpointProps;
import net.toknsmith.login.translator.ErrorResponseExceptionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.toknsmith.login.endpoint.entity.request.FormFields;
import net.toknsmith.login.endpoint.entity.request.GrantType;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;



public class UserEndpointTest {
    private static LoginFactory loginFactory = new LoginFactory();
    private UserEndpoint subject;
    @Mock
    private HttpClient mockHttpClient;
    @Mock
    private HttpUtils mockHttpUtils;
    @Mock
    private ErrorResponseExceptionFactory mockErrorResponseExceptionFactory;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        EndpointProps ep = new EndpointProps();
        ep.setClientCredentials("clientCredentials");
        ep.setTokenEndpoint(new URI("http://localhost:8009/api/v1/token"));
        ep.setUserInfoEndpoint(new URI("http://localhost:8009/api/v1/userinfo"));

        subject = new UserEndpoint(
                mockHttpClient,
                mockHttpUtils,
                ep,
                mockErrorResponseExceptionFactory,
                loginFactory.openIdTokenReader(),
                loginFactory.jwtAppFactory()
        );
    }

    @Test
    public void makePasswordFormShouldBeOk() throws Exception {
        String username = "obi-wan@tokensmith.net";
        String password = "password";

        List<String> scopes = new ArrayList<>();
        scopes.add("openid");
        scopes.add("profile");

        Map<String, List<String>> actual = subject.makePasswordForm(username, password, scopes);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(4));

        assertThat(actual.get(FormFields.GRANT_TYPE.toString()), is(notNullValue()));
        assertThat(actual.get(FormFields.GRANT_TYPE.toString()).size(), is(1));
        assertThat(actual.get(FormFields.GRANT_TYPE.toString()).get(0), is(GrantType.PASSWORD.toString()));

        assertThat(actual.get(FormFields.USERNAME.toString()), is(notNullValue()));
        assertThat(actual.get(FormFields.USERNAME.toString()).size(), is(1));
        assertThat(actual.get(FormFields.USERNAME.toString()).get(0), is(username));

        assertThat(actual.get(FormFields.PASSWORD.toString()), is(notNullValue()));
        assertThat(actual.get(FormFields.PASSWORD.toString()).size(), is(1));
        assertThat(actual.get(FormFields.PASSWORD.toString()).get(0), is(password));

        assertThat(actual.get(FormFields.SCOPE.toString()), is(notNullValue()));
        assertThat(actual.get(FormFields.SCOPE.toString()).size(), is(1));
        assertThat(actual.get(FormFields.SCOPE.toString()).get(0), is("openid profile"));
    }

    @Test
    public void makeRefreshFormShouldBeOk() throws Exception {

        Map<String, List<String>> actual = subject.makeRefreshForm("refresh_token_value");

        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(2));

        assertThat(actual.get(FormFields.GRANT_TYPE.toString()), is(notNullValue()));
        assertThat(actual.get(FormFields.GRANT_TYPE.toString()).size(), is(1));
        assertThat(actual.get(FormFields.GRANT_TYPE.toString()).get(0), is(GrantType.REFRESH_TOKEN.toString()));

        assertThat(actual.get(FormFields.REFRESH_TOKEN.toString()), is(notNullValue()));
        assertThat(actual.get(FormFields.REFRESH_TOKEN.toString()).size(), is(1));
        assertThat(actual.get(FormFields.REFRESH_TOKEN.toString()).get(0), is("refresh_token_value"));
    }

    @Test
    public void makeCodeFormShouldBeOk() throws Exception {

        String code = "my-super-secret-code";
        String redirectUri = "https://tokensmith.net/account";
        Map<String, List<String>> actual = subject.makeCodeForm(code, redirectUri);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(3));

        assertThat(actual.get(FormFields.GRANT_TYPE.toString()), is(notNullValue()));
        assertThat(actual.get(FormFields.GRANT_TYPE.toString()).size(), is(1));
        assertThat(actual.get(FormFields.GRANT_TYPE.toString()).get(0), is(GrantType.AUTHORIZATION_CODE.toString()));

        assertThat(actual.get(FormFields.CODE.toString()), is(notNullValue()));
        assertThat(actual.get(FormFields.CODE.toString()).size(), is(1));
        assertThat(actual.get(FormFields.CODE.toString()).get(0), is(code));

        assertThat(actual.get(FormFields.REDIRECT_URI.toString()), is(notNullValue()));
        assertThat(actual.get(FormFields.REDIRECT_URI.toString()).size(), is(1));
        assertThat(actual.get(FormFields.REDIRECT_URI.toString()).get(0), is(redirectUri));


    }

}
