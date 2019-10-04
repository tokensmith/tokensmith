package org.rootservices.authorization.persistence.mapper.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.*;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by tommackenzie on 2/27/16.
 */
public class OptionalOffsetDateTimeTypeHandler extends OptionalTypeHandler<OffsetDateTime> {

    private static DateTimeFormatter formatterForInsert = DateTimeFormatter.ofPattern("yyyy-MM-dd H:m:s.SSSSSxxx");

    @Override
    public int getJdbcDataType() {
        return Types.TIMESTAMP;
    }

    @Override
    public Optional<OffsetDateTime> makeT(String value) throws SQLException {
        Optional<OffsetDateTime> result = Optional.empty();

        List<String> datePatterns = new ArrayList<>();
        datePatterns.add("yyyy-MM-dd H:m:s.SSSSSSX");
        datePatterns.add("yyyy-MM-dd H:m:s.SSSSX");

        if (value != null) {
            for (String datePattern: datePatterns) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);
                try {
                    result = Optional.of(OffsetDateTime.parse(value, formatter));
                    break;
                } catch (DateTimeParseException stupidException) {
                    continue;
                }
            }
        }
        return result;
    }

    @Override
    public Object makeObject(Optional<OffsetDateTime> parameter) {
        return parameter.get().format(formatterForInsert);
    }
}
