package net.toknsmith.login.endpoint;


import net.toknsmith.login.HttpUtils;
import net.toknsmith.login.endpoint.entity.response.api.ClientError;
import net.toknsmith.login.endpoint.entity.response.api.ServerError;
import net.toknsmith.login.exception.TranslateException;
import net.tokensmith.jwt.entity.jwk.RSAPublicKey;
import net.toknsmith.login.exception.CommException;
import net.toknsmith.login.exception.URLException;
import net.toknsmith.login.exception.http.api.ClientException;
import net.toknsmith.login.exception.http.api.ServerException;
import net.toknsmith.login.http.ContentType;
import net.toknsmith.login.http.Header;
import net.toknsmith.login.http.HeaderValue;
import net.toknsmith.login.http.StatusCode;
import net.toknsmith.login.translator.JwtRSAPublicKeyTranslator;


import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;



public class KeyEndpoint {
    private static String COMM_MSG = "Failed to communicate with Identity Server";
    private JwtRSAPublicKeyTranslator keyTranslator;
    private HttpClient httpClient;
    private HttpUtils httpUtils;
    private String publicKeyEndpoint;

    public KeyEndpoint(JwtRSAPublicKeyTranslator keyTranslator, HttpClient httpClient, HttpUtils httpUtils, String publicKeyEndpoint) {
        this.keyTranslator = keyTranslator;
        this.httpClient = httpClient;
        this.httpUtils = httpUtils;
        this.publicKeyEndpoint = publicKeyEndpoint;
    }

    public RSAPublicKey getKey(String keyId) throws URLException, CommException, TranslateException, ServerException, ClientException {
        URI target = makeKeyEndpoint(keyId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(target)
                .timeout(Duration.ofSeconds(2))
                .header(Header.CONTENT_TYPE.toString(), ContentType.JSON_UTF_8.toString())
                .header(Header.ACCEPT.toString(), ContentType.JSON_UTF_8.toString())
                .header(Header.ACCEPT_ENCODING.toString(), "gzip")
                .header(Header.CORRELATION_ID.toString(), httpUtils.getCorrelationId())
                .header(Header.LOGIN_SDK.toString(), HeaderValue.LOGIN_SDK.toString())
                .header(Header.LOGIN_SDK_VERSION.toString(), HeaderValue.LOGIN_SDK_VERSION.toString())
                .GET()
                .build();

        HttpResponse<InputStream> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        } catch (IOException | InterruptedException e) {
            throw new CommException(COMM_MSG, e);
        }

        InputStream body = httpUtils.processResponse(response);

        if (response.statusCode() != StatusCode.OK.getCode()) {
            handleNotOk(response, body);
        }

        return keyTranslator.toSingle(body);
    }

    protected URI makeKeyEndpoint(String keyId) throws URLException {
        try {
            return new URI(String.format(publicKeyEndpoint, keyId));
        } catch (URISyntaxException e) {
            // rare this will occur b/c we check publicKeyEndpoint when
            // this is created in LoginFactory.
            String msg = "invalid url for: %s with keyId: %s";
            throw new URLException(String.format(msg, publicKeyEndpoint, keyId), e);
        }
    }

    protected void handleNotOk(HttpResponse<InputStream> response, InputStream decompressedBody) throws ClientException, ServerException {
        // TODO: figure out what the ClientError and ServerError shapes are.
        if (response.statusCode() >= StatusCode.BAD_REQUEST.getCode() && response.statusCode() < StatusCode.SERVER_ERROR.getCode()) {
            ClientError clientError = new ClientError();
            throw new ClientException("client error while communicating with id server.", response.statusCode(), clientError);
        } else if (response.statusCode() >= StatusCode.SERVER_ERROR.getCode()) {
            ServerError serverError = new ServerError();
            throw new ServerException("client error while communicating with id server.", response.statusCode(), serverError);
        }
    }
}
