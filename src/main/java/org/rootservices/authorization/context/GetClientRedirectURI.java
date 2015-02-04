package org.rootservices.authorization.context;

import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.net.URI;
import java.util.UUID;

/**
 * Created by tommackenzie on 2/3/15.
 */
public interface GetClientRedirectURI {
    public URI run(UUID uuid) throws RecordNotFoundException;
}
