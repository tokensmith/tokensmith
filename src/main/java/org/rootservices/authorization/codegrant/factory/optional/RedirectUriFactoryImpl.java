package org.rootservices.authorization.codegrant.factory.optional;

import org.apache.commons.validator.routines.UrlValidator;
import org.rootservices.authorization.codegrant.factory.constants.ValidationMessage;
import org.rootservices.authorization.codegrant.factory.exception.RedirectUriException;
import org.rootservices.authorization.codegrant.validator.OptionalParam;
import org.rootservices.authorization.codegrant.validator.exception.EmptyValueError;
import org.rootservices.authorization.codegrant.validator.exception.MoreThanOneItemError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Created by tommackenzie on 2/1/15.
 */
@Component
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
    public Optional<URI> makeRedirectUri(List<String> redirectUris) throws RedirectUriException {

        try {
            optionalParam.run(redirectUris);
        } catch (EmptyValueError e) {
            throw new RedirectUriException(ValidationMessage.EMPTY_VALUE.toString(), e);
        } catch (MoreThanOneItemError e) {
            throw new RedirectUriException(ValidationMessage.MORE_THAN_ONE_ITEM.toString(), e);
        }

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
            throw new RedirectUriException("Cannot coerce String to URI");
        }

        return uri;
    }
}
