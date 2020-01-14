package net.toknsmith.login.exception.http.api;

import net.toknsmith.login.endpoint.entity.response.api.ClientError;


/**
 * Represents a 4XX response from one of the apis in the ID Server.
 *
 * Apis are: /key
 * Apis are not: /token, /userinfo
 */
public class ClientException extends Exception {
    private Integer statusCode;
    private ClientError clientError;

    public ClientException(String message, Integer statusCode, ClientError clientError) {
        super(message);
        this.statusCode = statusCode;
        this.clientError = clientError;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public ClientError getClientError() {
        return clientError;
    }
}
