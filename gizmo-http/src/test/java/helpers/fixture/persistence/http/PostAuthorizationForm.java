package helpers.fixture.persistence.http;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Param;
import com.ning.http.client.Response;
import helpers.fixture.FormFactory;
import helpers.fixture.exception.GetCsrfException;
import net.tokensmith.otter.QueryStringToMap;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Created by tommackenzie on 7/22/15.
 */
public class PostAuthorizationForm {
    private AsyncHttpClient httpDriver;
    private GetSessionAndCsrfToken getSessionAndCsrfToken;

    public PostAuthorizationForm(AsyncHttpClient httpDriver, GetSessionAndCsrfToken getSessionAndCsrfToken) {
        this.httpDriver = httpDriver;
        this.getSessionAndCsrfToken = getSessionAndCsrfToken;
    }

    public String run(ConfidentialClient confidentialClient, String baseURI, List<String> scopes, String email) throws IOException, ExecutionException, InterruptedException, URISyntaxException, GetCsrfException {

        String servletURI = baseURI +
                "?client_id=" + confidentialClient.getClient().getId().toString() +
                "&response_type=" + confidentialClient.getClient().getResponseTypes().get(0).getName() +
                "&redirect_uri=" + URLEncoder.encode(confidentialClient.getClient().getRedirectURI().toString(), "UTF-8") +
                "&scope=";

        for(String scope: scopes) {
            servletURI += URLEncoder.encode(scope + " ", "UTF-8");
        }

        Session session = getSessionAndCsrfToken.run(servletURI);
        List<Param> postData = FormFactory.makeLoginForm(email, session.getCsrfToken());

        ListenableFuture<Response> f = httpDriver
                .preparePost(servletURI)
                .setFormParams(postData)
                .setCookies(Arrays.asList(session.getSession()))
                .execute();

        Response response = f.get();

        URI location = new URI(response.getHeader("location"));
        QueryStringToMap queryStringToMap = new QueryStringToMap();
        Map<String, List<String>> params = queryStringToMap.run(
                Optional.of(location.getQuery())
        );

        return params.get("code").get(0);
    }
}
