package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.entity.ResponseType;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;

/**
 * Created by tommackenzie on 8/9/16.
 */
public interface ResponseTypeRepository {
    ResponseType getByName(String name) throws RecordNotFoundException;
}
