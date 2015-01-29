package org.rootservices.authorization.codegrant.translator;

import org.rootservices.authorization.codegrant.translator.exception.EmptyValueError;
import org.rootservices.authorization.codegrant.translator.exception.InvalidValueError;
import org.rootservices.authorization.codegrant.translator.exception.ValidationError;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by tommackenzie on 1/22/15.
 */
@Component
public class StringsToURIImpl implements StringsToURI {
    @Override
    public URI run(List<String> items) throws EmptyValueError, InvalidValueError, ValidationError{

        // optional parameter.
        if( items.size() == 0 ) {
            return null;
        }

        if(items.get(0).isEmpty()) {
            throw new EmptyValueError("parameter is empty");
        }

        if(items.size() > 1) {
            throw new ValidationError("parameter has more than one item");
        }

        URI uri;
        try {
            uri = new URI(items.get(0));
        } catch (URISyntaxException e) {
            throw new InvalidValueError("parameter is not a URI");
        }

        return uri;
    }
}
