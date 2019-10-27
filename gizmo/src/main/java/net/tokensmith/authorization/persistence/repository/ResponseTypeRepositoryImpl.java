package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.entity.ResponseType;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;
import net.tokensmith.authorization.persistence.mapper.ResponseTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 8/9/16.
 */
@Component
public class ResponseTypeRepositoryImpl implements ResponseTypeRepository {

    private ResponseTypeMapper responseTypeMapper;

    @Autowired
    public ResponseTypeRepositoryImpl(ResponseTypeMapper responseTypeMapper) {
        this.responseTypeMapper = responseTypeMapper;
    }

    @Override
    public ResponseType getByName(String name) throws RecordNotFoundException {
        ResponseType responseType = responseTypeMapper.getByName(name);

        if (responseType == null) {
            throw new RecordNotFoundException("Response Type was not found");
        }

        return responseType;
    }
}
