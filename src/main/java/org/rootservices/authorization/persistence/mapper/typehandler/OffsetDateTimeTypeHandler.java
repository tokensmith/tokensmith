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

    private static DateTimeFormatter formatterForInsert = DateTimeFormatter.ofPattern("yyyy-MM-dd H:m:s.SSSSSxxx");

    @Override
    public void setParameter(PreparedStatement ps, int i, OffsetDateTime parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setObject(i, null, Types.TIMESTAMP);
        } else {
            String formattedDate = parameter.format(formatterForInsert);
            ps.setObject(i, formattedDate, Types.TIMESTAMP);
        }
    }

    @Override
    public OffsetDateTime getResult(ResultSet rs, String columnName) throws SQLException {
        // patterns cant dynamically determine length of Fraction of seconds.
        List<String> datePatterns = new ArrayList<>();
        datePatterns.add("yyyy-MM-dd H:m:s.SSSSSSX");
        datePatterns.add("yyyy-MM-dd H:m:s.SSSSX");
        datePatterns.add("yyyy-MM-dd H:m:s.SSSX");
        datePatterns.add("yyyy-MM-dd H:m:s.SSX");
        datePatterns.add("yyyy-MM-dd H:m:s.SX");

        String columnValue = rs.getString(columnName);
        OffsetDateTime result = null;
        if (columnValue != null) {
            for (String datePattern: datePatterns) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);
                try {
                    result = OffsetDateTime.parse(columnValue, formatter);
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
