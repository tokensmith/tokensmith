package net.tokensmith.repository.repo;

import net.tokensmith.repository.entity.ResponseType;
import net.tokensmith.repository.exceptions.RecordNotFoundException;

/**
 * Created by tommackenzie on 8/9/16.
 */
public interface ResponseTypeRepository {
    ResponseType getByName(String name) throws RecordNotFoundException;
}
