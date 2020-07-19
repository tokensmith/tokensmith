package net.tokensmith.authorization.persistence.mapper.typehandler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tommackenzie on 4/18/15.
 */
public class OffsetDateTimeTypeHandler implements TypeHandler<OffsetDateTime> {

    private static DateTimeFormatter formatterForInsert = DateTimeFormatter.ofPattern("yyyy-MM-dd H:m:s.SSSSSSxxx");
    private List<String> datePatterns = Arrays.asList(
            "yyyy-MM-dd H:m:s.SSSSSSX",
            "yyyy-MM-dd H:m:s.SSSSX",
            "yyyy-MM-dd H:m:s.SSSX",
            "yyyy-MM-dd H:m:s.SSX",
            "yyyy-MM-dd H:m:s.SX"
    );


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
        String columnValue = rs.getString(columnName);
        OffsetDateTime result = to(columnValue);
        return result;
    }

    @Override
    public OffsetDateTime getResult(ResultSet rs, int columnIndex) throws SQLException {
        return to(rs.getString(columnIndex));
    }

    @Override
    public OffsetDateTime getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return OffsetDateTime.parse(cs.getString(columnIndex));
    }

    protected OffsetDateTime to(String columnValue) {
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
}
