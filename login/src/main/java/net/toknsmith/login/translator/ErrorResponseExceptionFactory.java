package net.toknsmith.login.translator;

import net.toknsmith.login.exception.TranslateException;
import net.toknsmith.login.endpoint.entity.response.openid.TokenErrorResponse;
import net.toknsmith.login.exception.http.openid.BadRequestException;
import net.toknsmith.login.exception.http.openid.ErrorResponseException;
import net.toknsmith.login.exception.http.openid.NotFoundException;
import net.toknsmith.login.exception.http.openid.ServerError;
import net.toknsmith.login.exception.http.openid.UnAuthorizedException;
import net.toknsmith.login.http.StatusCode;

import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.Optional;

public class ErrorResponseExceptionFactory {
    private ErrorResponseTranslator errorResponseTranslator;

    public ErrorResponseExceptionFactory(ErrorResponseTranslator errorResponseTranslator) {
        this.errorResponseTranslator = errorResponseTranslator;
    }

    public ErrorResponseException forTokenEndpoint(HttpResponse<InputStream> response, InputStream body) {

        ErrorResponseException ere = null;

        // this is the expected error response body from the id server.
        TokenErrorResponse error;
        try {
            error = errorResponseTranslator.to(response, body);
        } catch (TranslateException e) {
            return new ErrorResponseException("invalid error response from server", e, response.statusCode(), Optional.empty());
        }

        if (response.statusCode() == StatusCode.BAD_REQUEST.getCode()) {
            ere = new BadRequestException(error);
        } else if (response.statusCode() == StatusCode.UNAUTHORIZED.getCode()) {
            ere = new UnAuthorizedException(error);
        } else if (response.statusCode() == StatusCode.NOT_FOUND.getCode()) {
            ere = new NotFoundException(error);
        } else if (response.statusCode() == StatusCode.SERVER_ERROR.getCode()) {
            ere = new ServerError(error);
        } else {
            ere = new ErrorResponseException(Optional.of(error), response.statusCode());
        }
        return ere;
    }

    public ErrorResponseException forUserEndpoint(int statusCode) {

        ErrorResponseException ere;
        if (statusCode == StatusCode.BAD_REQUEST.getCode()) {
            ere = new BadRequestException(Optional.empty());
        } else if (statusCode == StatusCode.UNAUTHORIZED.getCode()) {
            ere = new UnAuthorizedException(Optional.empty());
        } else if (statusCode == StatusCode.NOT_FOUND.getCode()) {
            ere = new NotFoundException(Optional.empty());
        } else if (statusCode == StatusCode.SERVER_ERROR.getCode()) {
            ere = new ServerError(Optional.empty());
        } else {
            ere = new ErrorResponseException(Optional.empty(), statusCode);
        }
        return ere;
    }
}
