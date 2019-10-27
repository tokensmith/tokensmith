package net.tokensmith.authorization.persistence.mapper.typehandler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.*;
import java.util.Optional;

/**
 * Created by tommackenzie on 2/28/16.
 */
public abstract class OptionalTypeHandler<T> implements TypeHandler<Optional<T>> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Optional<T> parameter, JdbcType jdbcType) throws SQLException {
        if ( parameter.isPresent() ) {
            ps.setObject(i, makeObject(parameter), getJdbcDataType());
        } else {
            ps.setObject(i, null, getJdbcDataType());
        }
    }

    @Override
    public Optional<T> getResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return makeT(value);
    }

    @Override
    public Optional<T> getResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return makeT(value);
    }

    @Override
    public Optional<T> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return makeT(value);
    }

    public abstract int getJdbcDataType();
    public abstract Optional<T> makeT(String value) throws SQLException;
    public abstract Object makeObject(Optional<T> parameter);
}
