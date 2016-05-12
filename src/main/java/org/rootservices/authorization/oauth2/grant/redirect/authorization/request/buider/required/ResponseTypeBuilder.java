package org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.required;

import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.exception.ResponseTypeException;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.util.List;

/**
 * Created by tommackenzie on 1/31/15.
 */
public interface ResponseTypeBuilder {
    ResponseType makeResponseType(List<String> responseTypes) throws ResponseTypeException;
}
