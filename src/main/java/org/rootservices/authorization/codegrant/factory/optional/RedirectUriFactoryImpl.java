package org.rootservices.authorization.codegrant.factory.optional;

import org.apache.commons.validator.routines.UrlValidator;
import org.rootservices.authorization.codegrant.factory.exception.DataTypeException;
import org.rootservices.authorization.codegrant.validator.OptionalParam;
import org.rootservices.authorization.codegrant.validator.exception.EmptyValueError;
import org.rootservices.authorization.codegrant.validator.exception.MoreThanOneItemError;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Created by tommackenzie on 2/1/15.
 */
public class RedirectUriFactoryImpl implements RedirectUriFactory {

    @Autowired
    private OptionalParam optionalParam;

    @Autowired
    private UrlValidator urlValidator;

    public RedirectUriFactoryImpl() {}

    public RedirectUriFactoryImpl(OptionalParam optionalParam, UrlValidator urlValidator) {
        this.optionalParam = optionalParam;
        this.urlValidator = urlValidator;
    }

    @Override
    public Optional<URI> makeRedirectUri(List<String> redirectUris) throws EmptyValueError, MoreThanOneItemError, DataTypeException {
        optionalParam.run(redirectUris);

        String uriCandidate;
        if (redirectUris == null) {
            return Optional.empty();
        } else {
            uriCandidate = redirectUris.get(0);
        }

        Optional<URI> uri;

        if ( urlValidator.isValid(uriCandidate)) {
            uri = Optional.ofNullable(URI.create(uriCandidate));
        } else {
            throw new DataTypeException("parameter is not a URI");
        }

        return uri;
    }
}
