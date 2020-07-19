package net.tokensmith.authorization.persistence.mapper.typehandler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by tommackenzie on 11/15/14.
 */
public class URITypeHandler implements TypeHandler<URI> {

    @Override
    public void setParameter(PreparedStatement ps, int i, URI parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setObject(i, null, Types.VARCHAR);
        } else {
            ps.setObject(i, parameter.toString(), Types.VARCHAR);
        }
    }

    @Override
    public URI getResult(ResultSet rs, String columnName) throws SQLException {
        if (rs.getString(columnName) != null) {
            try {
                return new URI(rs.getString(columnName));
            } catch (URISyntaxException e) {
                throw new SQLException("Retrieving code - URI is malformed");
            }
        }
        return null;
    }

    @Override
    public URI getResult(ResultSet rs, int columnIndex) throws SQLException {
        try {
            return new URI(rs.getString(columnIndex));
        } catch (URISyntaxException e) {
            throw new SQLException("Retrieving code - URI is malformed");
        }
    }

    @Override
    public URI getResult(CallableStatement cs, int columnIndex) throws SQLException {
        try {
            return new URI(cs.getString(columnIndex));
        } catch (URISyntaxException e) {
            throw new SQLException("Retrieving code - URI is malformed");
        }
    }
}