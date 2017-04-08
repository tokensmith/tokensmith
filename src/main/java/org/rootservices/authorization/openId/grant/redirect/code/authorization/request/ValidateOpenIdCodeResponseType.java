package org.rootservices.authorization.openId.grant.redirect.code.authorization.request;

import org.apache.commons.validator.routines.UrlValidator;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.openId.grant.redirect.code.authorization.request.context.GetOpenIdConfidentialClientRedirectUri;
import org.rootservices.authorization.openId.grant.redirect.code.authorization.request.factory.OpenIdCodeAuthRequestFactory;
import org.rootservices.authorization.openId.grant.redirect.code.authorization.request.entity.OpenIdAuthRequest;
import org.rootservices.authorization.parse.ParamEntity;
import org.rootservices.authorization.parse.Parser;
import org.rootservices.authorization.parse.exception.*;
import org.rootservices.authorization.parse.validator.excpeption.EmptyValueError;
import org.rootservices.authorization.parse.validator.excpeption.MoreThanOneItemError;
import org.rootservices.authorization.parse.validator.excpeption.NoItemsError;
import org.rootservices.authorization.parse.validator.excpeption.ParamIsNullError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by tommackenzie on 10/10/15.
 */
@Component
public class ValidateOpenIdCodeResponseType {
    private static String INFORM_CLIENT_MSG = "Authorization Request did not pass validation - client should be informed.";
    private static String CLIENT_ID = "client_id";
    private static String REDIRECT_URI = "redirect_uri";

    private Parser parser;
    private UrlValidator urlValidator;
    private GetOpenIdConfidentialClientRedirectUri getOpenIdConfidentialClientRedirectUri;
    private CompareConfidentialClientToOpenIdAuthRequest compareConfidentialClientToOpenIdAuthRequest;

    @Autowired
    public ValidateOpenIdCodeResponseType(Parser parser, UrlValidator urlValidator, GetOpenIdConfidentialClientRedirectUri getOpenIdConfidentialClientRedirectUri, CompareConfidentialClientToOpenIdAuthRequest compareConfidentialClientToOpenIdAuthRequest) {
        this.parser = parser;
        this.getOpenIdConfidentialClientRedirectUri = getOpenIdConfidentialClientRedirectUri;
        this.urlValidator = urlValidator;
        this.compareConfidentialClientToOpenIdAuthRequest = compareConfidentialClientToOpenIdAuthRequest;
    }

    public OpenIdAuthRequest run(Map<String, List<String>> parameters) throws InformResourceOwnerException, InformClientException {

        List<ParamEntity> fields = parser.reflect(OpenIdAuthRequest.class);
        OpenIdAuthRequest request = null;
        try {
            request = (OpenIdAuthRequest) parser.to(OpenIdAuthRequest.class, fields, parameters);
        } catch (RequiredException e) {
            handleRequired(e);
        } catch (OptionalException e) {
            handleOptional(e);
        } catch (ParseException e) {
            // TODO: unexpected thing occurred.
        }

        if (!urlValidator.isValid(request.getRedirectURI().toString())) {
            throw new InformResourceOwnerException("redirect_uri is not valid", 1);
        }

        compareConfidentialClientToOpenIdAuthRequest.run(request);

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
            throw new InformResourceOwnerException("", e);
        }
        OpenIdAuthRequest request = (OpenIdAuthRequest) e.getTarget();
        getOpenIdConfidentialClientRedirectUri.run(request.getClientId(), request.getRedirectURI(), e);

        throw new InformClientException(
                INFORM_CLIENT_MSG,
                errorFromParamAndCause(e.getParam(), e.getCause()),
                descFromCause(e.getParam(), e.getCause()),
                1,
                request.getRedirectURI(),
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
        OpenIdAuthRequest request = (OpenIdAuthRequest) e.getTarget();
        getOpenIdConfidentialClientRedirectUri.run(request.getClientId(), request.getRedirectURI(), e);

        throw new InformClientException(
                INFORM_CLIENT_MSG,
                errorFromParamAndCause(e.getParam(), e.getCause()),
                descFromCause(e.getParam(), e.getCause()),
                1,
                request.getRedirectURI(),
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
        } else if ("response_type".equals(param) || "state".equals(param) || "scope".equals(param)) {
            error = "invalid_request";
        }
        return error;
    }

    protected String descFromCause(String param, Throwable t) {
        String description = null;
        if (t instanceof EmptyValueError) {
            description = param + " is blank or missing";
        } else if (t instanceof MoreThanOneItemError) {
            description = param + " has more than one value.";
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
