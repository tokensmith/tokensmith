package helpers.fixture.persistence.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import org.rootservices.authorization.http.response.OpenIdToken;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by tommackenzie on 7/22/15.
 */
public class PostTokenRefreshGrant {
    private AsyncHttpClient httpDriver;
    private ObjectMapper objectMapper;

    public PostTokenRefreshGrant(AsyncHttpClient httpDriver, ObjectMapper objectMapper) {
        this.httpDriver = httpDriver;
        this.objectMapper = objectMapper;
    }

    public OpenIdToken run(ConfidentialClient confidentialClient, String tokenURI, String refreshToken) throws InterruptedException, ExecutionException, URISyntaxException, IOException {

        Map<String, List<String>> form = new HashMap<>();
        form.put("grant_type", Arrays.asList("refresh_token"));
        form.put("refresh_token", Arrays.asList(refreshToken));

        String credentials = confidentialClient.getClient().getId().toString() + ":password";

        String encodedCredentials = new String(
                Base64.getEncoder().encode(credentials.getBytes()),
                StandardCharsets.UTF_8
        );

        ListenableFuture<Response> f = httpDriver
                .preparePost(tokenURI)
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setHeader("Authorization", "Basic " + encodedCredentials)
                .setFormParams(form)
                .execute();

        Response response = f.get();
        OpenIdToken token = objectMapper.readValue(response.getResponseBody(), OpenIdToken.class);
        return token;
    }
}
