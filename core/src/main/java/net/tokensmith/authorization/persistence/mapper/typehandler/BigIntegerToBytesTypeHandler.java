package net.tokensmith.authorization.persistence.mapper.typehandler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.math.BigInteger;
import java.sql.*;

/**
 * Created by tommackenzie on 2/15/16.
 */
public class BigIntegerToBytesTypeHandler implements TypeHandler<BigInteger> {

    @Override
    public void setParameter(PreparedStatement ps, int i, BigInteger parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setObject(i, null, Types.BINARY);
        } else {
            ps.setObject(i, parameter.toByteArray(), Types.BINARY);
        }
    }

    @Override
    public BigInteger getResult(ResultSet rs, String s) throws SQLException {
        return new BigInteger(rs.getBytes(s));
    }

    @Override
    public BigInteger getResult(ResultSet rs, int columnIndex) throws SQLException {
        return new BigInteger(rs.getBytes(columnIndex));
    }

    @Override
    public BigInteger getResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        return new BigInteger(callableStatement.getBytes(columnIndex));
    }
}
