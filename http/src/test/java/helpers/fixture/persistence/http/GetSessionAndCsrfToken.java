package helpers.fixture.persistence.http;

import helpers.fixture.exception.GetCsrfException;
import io.netty.handler.codec.http.cookie.Cookie;
import net.tokensmith.authorization.http.controller.resource.html.CookieName;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GetSessionAndCsrfToken {
    private AsyncHttpClient httpDriver;

    // used for traditional forms.
    public static Pattern FORM_PATTERN = Pattern.compile(".*\"csrfToken\" value=\"([^\"]*)\".*", Pattern.DOTALL);

    // used on profile.jsp which api calls use.
    public static Pattern META_PATTERN = Pattern.compile(".*\"csrf-token\" content=\"([^\"]*)\".*", Pattern.DOTALL);

    public GetSessionAndCsrfToken(AsyncHttpClient httpDriver) {
        this.httpDriver = httpDriver;
    }

    public Session run(String uri) throws ExecutionException, InterruptedException, GetCsrfException {
        return run(uri, new ArrayList<>());
    }

    public Session run(String uri, List<Cookie> cookies) throws ExecutionException, InterruptedException, GetCsrfException {
        ListenableFuture<Response> f = httpDriver
                .prepareGet(uri)
                .setCookies(cookies)
                .execute();

        Response response = f.get();

        if ( response.getStatusCode() != 200 ) {
            URI redirectUri = null;
            if (response.getHeader("location") != null) {
                redirectUri = URI.create(response.getHeader("location"));
            }

            throw new GetCsrfException(
                    "getting csrf failed",
                    response.getStatusCode(),
                    redirectUri,
                    response.getResponseBody());
        }

        Session session = new Session();
        for(Cookie cookie: response.getCookies()) {
            if ("csrfToken".equals(cookie.name())) {
                session.setCsrf(cookie);
            }
            if ("session".equals(cookie.name())) {
                session.setSession(cookie);
            }
            if (CookieName.REDIRECT.toString().equals(cookie.name())) {
                session.setRedirect(cookie);
            }
        }

        if (Objects.isNull(session.getCsrf())) {
            throw new GetCsrfException(
                    "could not find the CSRF cookie",
                    response.getStatusCode(),
                    null,
                    response.getResponseBody());
        }

        session.setCsrfToken(extractCsrfToken(response.getResponseBody()).get());
        return session;
    }

    public Optional<String> extractCsrfToken(String responseBody) {
        Optional<String> csrfToken;
        Matcher formMatcher = FORM_PATTERN.matcher(responseBody);
        csrfToken = formMatcher.matches() ? Optional.of(formMatcher.group(1)) : Optional.empty();

        if (csrfToken.isEmpty()) {
            // could be as a meta tag.
            Matcher metaMatcher = META_PATTERN.matcher(responseBody);
            csrfToken = metaMatcher.matches() ? Optional.of(metaMatcher.group(1)) : Optional.empty();
        }
        return csrfToken;
    }
}
