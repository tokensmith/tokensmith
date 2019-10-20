package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

/**
 * Created by tommackenzie on 8/9/16.
 */
public interface ResponseTypeRepository {
    ResponseType getByName(String name) throws RecordNotFoundException;
}
