package org.rootservices.authorization.openId.grant.code.authorization.request.builder.required;

import org.apache.commons.validator.routines.UrlValidator;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.buider.exception.RedirectUriException;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.buider.validator.RequiredParam;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.buider.validator.exception.EmptyValueError;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.buider.validator.exception.MoreThanOneItemError;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.buider.validator.exception.NoItemsError;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.buider.validator.exception.ParamIsNullError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;


/**
 * Created by tommackenzie on 2/1/15.
 */
@Component
public class OpenIdRedirectUriBuilderImpl implements OpenIdRedirectUriBuilder {

    private RequiredParam requiredParam;
    private UrlValidator urlValidator;

    @Autowired
    public OpenIdRedirectUriBuilderImpl(RequiredParam requiredParam, UrlValidator urlValidator) {
        this.requiredParam = requiredParam;
        this.urlValidator = urlValidator;
    }

    @Override
    public URI build(List<String> redirectUris) throws RedirectUriException {

        try {
            requiredParam.run(redirectUris);
        } catch (EmptyValueError e) {
            throw new RedirectUriException(ErrorCode.REDIRECT_URI_EMPTY_VALUE, e);
        } catch (MoreThanOneItemError e) {
            throw new RedirectUriException(ErrorCode.REDIRECT_URI_MORE_THAN_ONE_ITEM, e);
        } catch (NoItemsError e) {
            throw new RedirectUriException(ErrorCode.REDIRECT_URI_EMPTY_LIST, e);
        } catch (ParamIsNullError e) {
            throw new RedirectUriException(ErrorCode.REDIRECT_URI_NULL, e);
        }

        String uriCandidate;
        uriCandidate = redirectUris.get(0);

        URI uri;
        if ( urlValidator.isValid(uriCandidate)) {
            uri = URI.create(uriCandidate);
        } else {
            throw new RedirectUriException(ErrorCode.REDIRECT_URI_DATA_TYPE);
        }

        return uri;
    }
}
