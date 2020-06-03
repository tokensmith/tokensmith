package net.tokensmith.authorization.persistence.mapper.typehandler;

import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Created by tommackenzie on 2/26/16.
 */
public class OptionalLocalDateTypeHandler extends OptionalTypeHandler<LocalDate> {

    @Override
    public int getJdbcDataType() {
        return Types.DATE;
    }

    @Override
    public Optional<LocalDate> makeT(String value) throws SQLException {

        Optional<LocalDate> localDate = Optional.empty();
        if (value != null) {
            localDate = Optional.of(LocalDate.parse(value));
        }
        return localDate;
    }

    @Override
    public Object makeObject(Optional<LocalDate> parameter) {
        return parameter.get();
    }
}
