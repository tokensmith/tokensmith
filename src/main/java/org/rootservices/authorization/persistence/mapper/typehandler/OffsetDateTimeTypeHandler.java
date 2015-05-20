package org.rootservices.authorization.persistence.mapper.typehandler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.*;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by tommackenzie on 4/18/15.
 */
public class OffsetDateTimeTypeHandler implements TypeHandler<OffsetDateTime> {

    @Override
    public void setParameter(PreparedStatement ps, int i, OffsetDateTime parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setObject(i, null, Types.DATE);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd H:m:sZ");
            ps.setObject(i, parameter.format(formatter), Types.DATE);
        }
    }

    @Override
    public OffsetDateTime getResult(ResultSet rs, String columnName) throws SQLException {
        if (rs.getString(columnName) != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd H:m:s.SSSSSSX");
            return OffsetDateTime.parse(rs.getString(columnName), formatter);
        }
        return null;
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
