package org.rootservices.authorization.persistence.mapper.typehandler;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 3/15/16.
 */
public class OptionalUUIDTypeHandler extends OptionalTypeHandler<UUID> {
    @Override
    public int getJdbcDataType() {
        return Types.OTHER;
    }

    @Override
    public Optional<UUID> makeT(String value) throws SQLException {
        Optional<UUID> uuid = Optional.empty();
        if ( value != null ) {
            uuid = Optional.ofNullable(UUID.fromString(value));
        }
        return uuid;
    }

    @Override
    public Object makeObject(Optional<UUID> parameter) {
        return parameter.get().toString();
    }
}
