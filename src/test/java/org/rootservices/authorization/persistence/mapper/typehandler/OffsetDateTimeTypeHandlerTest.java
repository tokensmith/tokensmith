package org.rootservices.authorization.persistence.mapper.typehandler;

import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 5/14/15.
 */
public class OffsetDateTimeTypeHandlerTest {

    private OffsetDateTimeTypeHandler subject;

    @Before
    public void setUp() {
        subject = new OffsetDateTimeTypeHandler();
    }

    @Test
    public void testGetResult() throws Exception {
        String columnName = "SCOPE_created_at";
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(columnName)).thenReturn("2015-05-14 07:53:52.782889-05");
        OffsetDateTime actual = subject.getResult(resultSet, columnName);
        assertThat(actual.getYear()).isEqualTo(2015);
    }
}