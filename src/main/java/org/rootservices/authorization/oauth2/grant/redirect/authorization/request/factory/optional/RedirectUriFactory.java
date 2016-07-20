package org.rootservices.authorization.oauth2.grant.redirect.authorization.request.factory.optional;

import org.apache.commons.validator.routines.UrlValidator;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.factory.exception.RedirectUriException;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.factory.validator.OptionalParam;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.factory.validator.exception.EmptyValueError;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.factory.validator.exception.MoreThanOneItemError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Created by tommackenzie on 2/1/15.
 */
@Component
public class RedirectUriFactory {

    @Autowired
    private OptionalParam optionalParam;
    @Autowired
    private UrlValidator urlValidator;

    public RedirectUriFactory() {
    }

    public RedirectUriFactory(OptionalParam optionalParam, UrlValidator urlValidator) {
        this.optionalParam = optionalParam;
        this.urlValidator = urlValidator;
    }

    public Optional<URI> makeRedirectUri(List<String> redirectUris) throws RedirectUriException {

        try {
            optionalParam.run(redirectUris);
        } catch (EmptyValueError e) {
            throw new RedirectUriException(ErrorCode.REDIRECT_URI_EMPTY_VALUE, e);
        } catch (MoreThanOneItemError e) {
            throw new RedirectUriException(ErrorCode.REDIRECT_URI_MORE_THAN_ONE_ITEM, e);
        }

        String uriCandidate;
        if (redirectUris == null || redirectUris.isEmpty()) {
            return Optional.ofNullable(null);
        } else {
            uriCandidate = redirectUris.get(0);
        }

        Optional<URI> uri;

        if ( urlValidator.isValid(uriCandidate)) {
            uri = Optional.ofNullable(URI.create(uriCandidate));
        } else {
            throw new RedirectUriException(ErrorCode.REDIRECT_URI_DATA_TYPE);
        }

        return uri;
    }
}
