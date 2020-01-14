package helper.fake;


import javax.net.ssl.SSLSession;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;


public class FakeHttpResponse<T> implements HttpResponse<T> {
    final int responseCode;
    final HttpRequest initialRequest;
    final Optional<HttpResponse<T>> previousResponse;
    final HttpHeaders headers;
    final Optional<SSLSession> sslSession;
    final URI uri;
    final HttpClient.Version version;
    final T body;

    public FakeHttpResponse(int responseCode, HttpRequest initialRequest, Optional<HttpResponse<T>> previousResponse, HttpHeaders headers, Optional<SSLSession> sslSession, URI uri, HttpClient.Version version, T body) {
        this.responseCode = responseCode;
        this.initialRequest = initialRequest;
        this.previousResponse = previousResponse;
        this.headers = headers;
        this.sslSession = sslSession;
        this.uri = uri;
        this.version = version;
        this.body = body;
    }

    @Override
    public int statusCode() {
        return responseCode;
    }

    @Override
    public HttpRequest request() {
        return initialRequest;
    }

    @Override
    public Optional<HttpResponse<T>> previousResponse() {
        return previousResponse;
    }

    @Override
    public HttpHeaders headers() {
        return headers;
    }

    @Override
    public T body() {
        return body;
    }

    @Override
    public Optional<SSLSession> sslSession() {
        return sslSession;
    }

    @Override
    public URI uri() {
        return uri;
    }

    @Override
    public HttpClient.Version version() {
        return version;
    }
}
