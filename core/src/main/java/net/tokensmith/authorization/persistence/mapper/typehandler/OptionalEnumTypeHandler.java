package net.tokensmith.authorization.persistence.mapper.typehandler;

import java.sql.*;
import java.util.Optional;

/**
 * Created by tommackenzie on 2/27/16.
 */
public class OptionalEnumTypeHandler<T extends Enum<T>> extends OptionalTypeHandler<T> {

    private Class<T> type;

    public OptionalEnumTypeHandler() {}

    public OptionalEnumTypeHandler(Class<T> type) {
        this.type = type;
    }

    @Override
    public int getJdbcDataType() {
        return Types.VARCHAR;
    }

    @Override
    public Optional<T> makeT(String value) {
        if (value == null) {
            return Optional.empty();
        } else {
            T enumInstance = (T) Enum.valueOf(type, value);
            return Optional.of(enumInstance);
        }
    }

    @Override
    public Object makeObject(Optional<T> parameter) {
        return parameter.get().name();
    }
}
