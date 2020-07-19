package helpers.fixture.persistence.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.tokensmith.authorization.http.response.OpenIdToken;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tommackenzie on 10/18/16.
 */
public class PostTokenPasswordGrant {
    private AsyncHttpClient httpDriver;
    private ObjectMapper om;
    private Base64.Encoder encoder;

    public PostTokenPasswordGrant(AsyncHttpClient httpDriver, ObjectMapper om, Base64.Encoder encoder) {
        this.httpDriver = httpDriver;
        this.om = om;
        this.encoder = encoder;
    }

    public OpenIdToken post(String user, String password, String scope, String client, String clientPassword, String servletURI) throws Exception {
        Map<String, List<String>> form = new HashMap<>();
        form.put("grant_type", Arrays.asList("password"));
        form.put("username", Arrays.asList(user));
        form.put("password", Arrays.asList(password));
        form.put("scope", Arrays.asList(scope));

        String credentials = client + ":" + clientPassword;
        String encodedCreds = new String(encoder.encode(credentials.getBytes()), StandardCharsets.UTF_8);

        ListenableFuture<Response> f = httpDriver
                .preparePost(servletURI)
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setHeader("Authorization", "Basic " + encodedCreds)
                .setFormParams(form)
                .execute();

        Response response = f.get();

        OpenIdToken token = om.readValue(response.getResponseBody(), OpenIdToken.class);
        return token;
    }
}
