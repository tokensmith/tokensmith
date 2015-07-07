package org.rootservices.authorization.persistence.mapper.typehandler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.Optional;

/**
 * Created by tommackenzie on 4/15/15.
 */
public class OptionalURITypeHandler implements TypeHandler<Optional<URI>> {
    @Override
    public void setParameter(PreparedStatement ps, int i, Optional<URI> parameter, JdbcType jdbcType) throws SQLException {
        if (parameter.isPresent()) {
            ps.setObject(i, parameter.get().toString(), Types.VARCHAR);
        } else {
            ps.setObject(i, null, Types.VARCHAR);
        }
    }

    @Override
    public Optional<URI> getResult(ResultSet rs, String columnName) throws SQLException {
        if (rs.getString(columnName) != null) {
            try {
                return Optional.ofNullable(new URI(rs.getString(columnName)));
            } catch (URISyntaxException e) {
                throw new SQLException("Retrieving code - URI is malformed");
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<URI> getResult(ResultSet rs, int columnIndex) throws SQLException {
        if ( rs.getString(columnIndex) != null) {
            try {
                return Optional.ofNullable(new URI(rs.getString(columnIndex)));
            } catch (URISyntaxException e) {
                throw new SQLException("Retrieving code - URI is malformed");
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<URI> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        if (cs.getString(columnIndex) != null) {
            try {
                return Optional.ofNullable(new URI(cs.getString(columnIndex)));
            } catch (URISyntaxException e) {
                throw new SQLException("Retrieving code - URI is malformed");
            }
        }
        return Optional.empty();
    }
}
