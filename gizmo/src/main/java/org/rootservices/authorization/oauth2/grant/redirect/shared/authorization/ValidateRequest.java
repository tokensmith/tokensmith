package org.rootservices.authorization.oauth2.grant.redirect.shared.authorization;

import org.apache.commons.validator.routines.UrlValidator;
import org.rootservices.authorization.exception.ServerException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.CompareClientToAuthRequest;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.context.GetClientRedirectUri;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.entity.AuthRequest;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.parse.ParamEntity;
import org.rootservices.authorization.parse.Parser;
import org.rootservices.authorization.parse.exception.OptionalException;
import org.rootservices.authorization.parse.exception.ParseException;
import org.rootservices.authorization.parse.exception.RequiredException;
import org.rootservices.authorization.parse.exception.ValueException;
import org.rootservices.authorization.parse.validator.excpeption.EmptyValueError;
import org.rootservices.authorization.parse.validator.excpeption.MoreThanOneItemError;
import org.rootservices.authorization.parse.validator.excpeption.NoItemsError;
import org.rootservices.authorization.parse.validator.excpeption.ParamIsNullError;


import java.net.URI;
import java.util.List;
import java.util.Map;


public class ValidateRequest {
    private static String PARSE_ERROR = "Unhandled parse error";
    private static String INFORM_CLIENT_MSG = "Authorization Request did not pass validation - client should be informed.";
    private static String CLIENT_ID = "client_id";
    private static String REDIRECT_URI = "redirect_uri";

    protected Parser parser;
    protected UrlValidator urlValidator;
    protected GetClientRedirectUri getClientRedirect;
    protected CompareClientToAuthRequest compareClientToAuthRequest;
    private List<ParamEntity> fields;

    public ValidateRequest(Parser parser, UrlValidator urlValidator, GetClientRedirectUri getClientRedirect, CompareClientToAuthRequest compareClientToAuthRequest) {
        this.parser = parser;
        this.urlValidator = urlValidator;
        this.getClientRedirect = getClientRedirect;
        this.compareClientToAuthRequest = compareClientToAuthRequest;
    }

    public AuthRequest run(Map<String, List<String>> parameters) throws InformResourceOwnerException, InformClientException, ServerException {

        if (fields == null) {
            fields = parser.reflect(AuthRequest.class);
        }

        AuthRequest request = null;
        try {
            request = (AuthRequest) parser.to(AuthRequest.class, fields, parameters);
        } catch (RequiredException e) {
            handleRequired(e);
        } catch (OptionalException e) {
            if (REDIRECT_URI.equals(e.getParam())) {
                throw new InformResourceOwnerException("", e, 1);
            }

            handleOptional(e);
        } catch (ParseException e) {
            throw new ServerException(PARSE_ERROR, e);
        }

        if (request.getRedirectURI().isPresent() && !urlValidator.isValid(request.getRedirectURI().get().toString())) {
            throw new InformResourceOwnerException("redirect_uri is not valid", 1);
        }

        compareClientToAuthRequest.run(request);

        return request;
    }

    /**
     * Throw a InformResourceOwnerException if the failed param is:
     * - client_id
     * - redirect_uri
     *
     * Throw a InformResourceOwnerException if the values of client_id and redirect_uri
     * do NOT match values in the database.
     *
     * Throw a InformClientException if the value of client_id and redirect_uri
     * matches values in the database.
     *
     * @param e
     * @throws InformClientException
     * @throws InformResourceOwnerException
     */
    protected void handleRequired(RequiredException e) throws InformClientException, InformResourceOwnerException {
        if (CLIENT_ID.equals(e.getParam()) || REDIRECT_URI.equals(e.getParam())) {
            throw new InformResourceOwnerException("", e, 1);
        }
        AuthRequest request = (AuthRequest) e.getTarget();
        URI clientRedirectUri = getClientRedirect.run(request.getClientId(), request.getRedirectURI(), e);

        throw new InformClientException(
                INFORM_CLIENT_MSG,
                errorFromParamAndCause(e.getParam(), e.getCause()),
                descFromCause(e.getParam(), e.getCause()),
                1,
                clientRedirectUri,
                request.getState(),
                e
        );
    }

    /**
     * Throw a InformResourceOwnerException if the values of client_id and redirect_uri
     * do NOT match values in the database.
     *
     * Throw a InformClientException if the value of client_id and redirect_uri
     * matches values in the database.
     *
     * @param e
     * @throws InformClientException
     * @throws InformResourceOwnerException
     */
    protected void handleOptional(OptionalException e) throws InformClientException, InformResourceOwnerException {
        AuthRequest request = (AuthRequest) e.getTarget();
        URI clientRedirectUri = getClientRedirect.run(request.getClientId(), request.getRedirectURI(), e);

        throw new InformClientException(
                INFORM_CLIENT_MSG,
                errorFromParamAndCause(e.getParam(), e.getCause()),
                descFromCause(e.getParam(), e.getCause()),
                1,
                clientRedirectUri,
                request.getState(),
                e
        );
    }

    protected String errorFromParamAndCause(String param, Throwable t) {
        String error = null;
        if ("scope".equals(param) && t instanceof EmptyValueError) {
            error = "invalid_scope";
        } else if ("response_type".equals(param) && t instanceof ValueException) {
            error = "unsupported_response_type";
        } else if ("response_type".equals(param) || "state".equals(param) || "scope".equals(param) || "nonce".equals(param)) {
            error = "invalid_request";
        }
        return error;
    }

    protected String descFromCause(String param, Throwable t) {
        String description = null;
        if (t instanceof EmptyValueError) {
            description = param + " is blank or missing";
        } else if (t instanceof MoreThanOneItemError) {
            description = param + " has more than one value";
        } else if (t instanceof ParamIsNullError) {
            description = param + " is null";
        } else if (t instanceof NoItemsError) {
            description = param + " is blank or missing";
        } else if (t instanceof ValueException) {
            description = param + " is invalid";
        }
        return description;
    }
}
