package helpers.fixture.exception;

import java.net.URI;

/**
 * Created by tommackenzie on 10/28/15.
 */
public class GetCsrfException extends Exception {
    private int statusCode;
    private URI redirectUri;
    private String responseBody;

    public GetCsrfException(String message, int statusCode, URI redirectUri, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.redirectUri = redirectUri;
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public URI getRedirectUri() {
        return redirectUri;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
