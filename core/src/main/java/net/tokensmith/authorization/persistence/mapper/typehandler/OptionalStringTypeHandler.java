package net.tokensmith.authorization.persistence.mapper.typehandler;

import java.sql.*;
import java.util.Optional;

/**
 * Created by tommackenzie on 2/26/16.
 */
public class OptionalStringTypeHandler extends OptionalTypeHandler<String> {

    @Override
    public int getJdbcDataType() {
        return Types.VARCHAR;
    }

    @Override
    public Optional<String> makeT(String value) throws SQLException {
        Optional<String> string = Optional.empty();
        if (value != null) {
            string = Optional.of(new String(value));
        }
        return string;
    }

    @Override
    public Object makeObject(Optional<String> parameter) {
        return parameter.get();
    }
}
