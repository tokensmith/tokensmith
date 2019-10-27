package net.tokensmith.authorization.persistence.mapper.typehandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.Optional;

/**
 * Created by tommackenzie on 4/15/15.
 */
public class OptionalURITypeHandler extends OptionalTypeHandler<URI> {

    @Override
    public int getJdbcDataType() {
        return Types.VARCHAR;
    }

    @Override
    public Optional<URI> makeT(String value) throws SQLException {
        Optional<URI> uri = Optional.empty();
        if ( value != null ) {
            try {
                uri = Optional.ofNullable(new URI(value));
            } catch (URISyntaxException e) {
                throw new SQLException("Retrieving code - URI is malformed");
            }
        }
        return uri;
    }

    @Override
    public Object makeObject(Optional<URI> parameter) {
        return parameter.get().toString();
    }
}
