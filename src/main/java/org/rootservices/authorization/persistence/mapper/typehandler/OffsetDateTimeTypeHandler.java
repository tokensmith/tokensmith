package org.rootservices.authorization.persistence.mapper.typehandler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.*;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tommackenzie on 4/18/15.
 */
public class OffsetDateTimeTypeHandler implements TypeHandler<OffsetDateTime> {

    @Override
    public void setParameter(PreparedStatement ps, int i, OffsetDateTime parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setObject(i, null, Types.TIMESTAMP);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd H:m:s.SSSSSSZ");
            String formattedDate = parameter.format(formatter);
            ps.setObject(i, formattedDate, Types.TIMESTAMP);
        }
    }

    @Override
    public OffsetDateTime getResult(ResultSet rs, String columnName) throws SQLException {
        // patterns cant dynamically determine length of Fraction of seconds.
        List<String> datePatterns = new ArrayList<>();
        datePatterns.add("yyyy-MM-dd H:m:s.SSSSSSX");
        datePatterns.add("yyyy-MM-dd H:m:s.SSSSX");

        OffsetDateTime result = null;
        if (rs.getString(columnName) != null) {
            for (String datePattern: datePatterns) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);
                try {
                    result = OffsetDateTime.parse(rs.getString(columnName), formatter);
                    break;
                } catch (DateTimeParseException stupidException) {
                    continue;
                }
            }
        }
        return result;
    }

    @Override
    public OffsetDateTime getResult(ResultSet rs, int columnIndex) throws SQLException {
        return OffsetDateTime.parse(rs.getString(columnIndex));
    }

    @Override
    public OffsetDateTime getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return OffsetDateTime.parse(cs.getString(columnIndex));
    }
}
