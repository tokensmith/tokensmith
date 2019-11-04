package helpers.fixture.persistence.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import net.tokensmith.authorization.http.response.OpenIdToken;
import net.tokensmith.repository.entity.ConfidentialClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by tommackenzie on 7/22/15.
 */
public class PostTokenCodeGrant {
    private AsyncHttpClient httpDriver;
    private ObjectMapper objectMapper;

    public PostTokenCodeGrant(AsyncHttpClient httpDriver, ObjectMapper objectMapper) {
        this.httpDriver = httpDriver;
        this.objectMapper = objectMapper;
    }

    public OpenIdToken run(ConfidentialClient confidentialClient, String tokenURI, String authorizationCode) throws InterruptedException, ExecutionException, URISyntaxException, IOException {

        Map<String, List<String>> form = new HashMap<>();
        form.put("grant_type", Arrays.asList("authorization_code"));
        form.put("code", Arrays.asList(authorizationCode));
        form.put("redirect_uri", Arrays.asList(confidentialClient.getClient().getRedirectURI().toString()));

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
