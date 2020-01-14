package net.toknsmith.login.exception.http.api;

import net.toknsmith.login.endpoint.entity.response.api.ServerError;


/**
 * Represents a 5xx response from one of the apis in the ID Server.
 *
 * Apis are: /key
 * Apis are not: /token, /userinfo
 */
public class ServerException extends Exception {
    private Integer statusCode;
    private ServerError serverError;

    public ServerException(String message, Integer statusCode, ServerError serverError) {
        super(message);
        this.statusCode = statusCode;
        this.serverError = serverError;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public ServerError getServerError() {
        return this.serverError;
    }
}
