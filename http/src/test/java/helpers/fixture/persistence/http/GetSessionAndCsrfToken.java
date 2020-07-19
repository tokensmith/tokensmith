package helpers.fixture.persistence.http;

import helpers.fixture.exception.GetCsrfException;
import io.netty.handler.codec.http.cookie.Cookie;
import net.tokensmith.authorization.http.controller.resource.html.CookieName;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GetSessionAndCsrfToken {
    private AsyncHttpClient httpDriver;
    private static Pattern PATTERN = Pattern.compile(".*\"csrfToken\" value=\"([^\"]*)\".*", Pattern.DOTALL);

    public GetSessionAndCsrfToken(AsyncHttpClient httpDriver) {
        this.httpDriver = httpDriver;
    }

    public Session run(String uri) throws IOException, ExecutionException, InterruptedException, GetCsrfException {
        ListenableFuture<Response> f = httpDriver
                .prepareGet(uri)
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
            if (cookie.name().equals("csrfToken")) {
                session.setCsrf(cookie);
            }
            if (cookie.name().equals("session")) {
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
        Optional<String> csrfToken = Optional.empty();
        Matcher m = PATTERN.matcher(responseBody);
        if (m.matches()) {
            csrfToken = Optional.of(m.group(1));
        }
        return csrfToken;


    }
}
