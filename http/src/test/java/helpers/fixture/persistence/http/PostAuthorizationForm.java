package helpers.fixture.persistence.http;

import helpers.fixture.FormFactory;
import helpers.fixture.exception.GetCsrfException;
import helpers.fixture.persistence.http.input.AuthEndpointProps;
import io.netty.handler.codec.http.cookie.Cookie;
import net.tokensmith.authorization.http.controller.resource.html.CookieName;
import net.tokensmith.otter.QueryStringToMap;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Param;
import org.asynchttpclient.Response;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tommackenzie on 7/22/15.
 */
public class PostAuthorizationForm {
    private static Pattern PATTERN = Pattern.compile(".*\"csrfToken\" value=\"([^\"]*)\".*", Pattern.DOTALL);
    public static final String UTF_8 = "UTF-8";
    private AsyncHttpClient httpDriver;
    private GetSessionAndCsrfToken getSessionAndCsrfToken;

    public PostAuthorizationForm(AsyncHttpClient httpDriver, GetSessionAndCsrfToken getSessionAndCsrfToken) {
        this.httpDriver = httpDriver;
        this.getSessionAndCsrfToken = getSessionAndCsrfToken;
    }

    public String run(AuthEndpointProps props) throws IOException, ExecutionException, InterruptedException, URISyntaxException, GetCsrfException {
        Response response = postLogin(props);

        URI location = new URI(response.getHeader("location"));
        QueryStringToMap queryStringToMap = new QueryStringToMap();
        Map<String, List<String>> params = queryStringToMap.run(
                Optional.of(location.getQuery())
        );

        return params.get("code").get(0);
    }

    public Session getSessionForProfile(AuthEndpointProps props, String nextURI) throws InterruptedException, ExecutionException, IOException, GetCsrfException {
        Response response = postLogin(props);

        // makes this call in order to get the csrf-token from /profile.
        Session profileSession = getSessionAndCsrfToken.run(
                nextURI, response.getCookies()
        );

        for(Cookie cookie: response.getCookies()) {
            if ("csrfToken".equals(cookie.name())) {
                profileSession.setCsrf(cookie);
            }
            if ("session".equals(cookie.name())) {
                profileSession.setSession(cookie);
            }
            if (CookieName.REDIRECT.toString().equals(cookie.name())) {
                profileSession.setRedirect(cookie);
            }
        }
        return profileSession;
    }

    protected Response postLogin(AuthEndpointProps props) throws IOException, ExecutionException, InterruptedException, GetCsrfException {
        String authEndpoint = authEndpoint(props);

        Session session = getSessionAndCsrfToken.run(authEndpoint);

        List<Cookie> cookies = new ArrayList<>();
        cookies.add(session.getRedirect());
        cookies.add(session.getCsrf());

        List<Param> postData = FormFactory.makeLoginForm(props.getEmail(), session.getCsrfToken());

        ListenableFuture<Response> f = httpDriver
                .preparePost(authEndpoint)
                .setFormParams(postData)
                .setCookies(cookies)
                .execute();

        return f.get();
    }

    public String authEndpoint(AuthEndpointProps props) throws IOException {
        String redirectURI = URLEncoder.encode(props.getConfidentialClient().getClient().getRedirectURI().toString(), UTF_8);
        StringBuilder authEndpointBuilder = new StringBuilder()
                .append(props.getBaseURI()).append("?")
                .append("client_id=").append(props.getConfidentialClient().getClient().getId().toString()).append("&")
                .append("response_type=").append(props.getConfidentialClient().getClient().getResponseTypes().get(0).getName()).append("&")
                .append("redirect_uri=").append(redirectURI).append("&")
                .append("scope=");

        for(String scope: props.getScopes()) {
            authEndpointBuilder.append(URLEncoder.encode(scope + " ", UTF_8));
        }

        // add extra params, maybe nonce, state, etc
        for(Map.Entry<String, List<String>> param: props.getParams().entrySet()) {
            for(String value: param.getValue()) {
                authEndpointBuilder
                    .append("&")
                    .append(param.getKey()).append("=").append(value);
            }
        }

        return authEndpointBuilder.toString();
    }
}
