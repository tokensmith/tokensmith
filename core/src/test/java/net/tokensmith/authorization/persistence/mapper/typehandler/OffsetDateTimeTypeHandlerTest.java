package net.tokensmith.authorization.persistence.mapper.typehandler;

import org.apache.ibatis.type.JdbcType;
import org.junit.Before;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
    public void testSetParameterZeroOffset() throws SQLException {
        String timeStampZeroOffset = "2015-05-14T11:58:22.173960+00:00";
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        OffsetDateTime dateTime = OffsetDateTime.parse(timeStampZeroOffset);

        subject.setParameter(mockPreparedStatement, 1, dateTime, JdbcType.TIMESTAMP);
        verify(mockPreparedStatement, times(1)).setObject(
                1, "2015-05-14 11:58:22.173960+00:00", Types.TIMESTAMP
        );
    }

    @Test
    public void testSetParameterMinusSixOffset() throws SQLException {
        String timeStampZeroOffset = "2015-05-14T11:58:22.173960-06:00";
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        OffsetDateTime dateTime = OffsetDateTime.parse(timeStampZeroOffset);

        subject.setParameter(mockPreparedStatement, 1, dateTime, JdbcType.TIMESTAMP);
        verify(mockPreparedStatement, times(1)).setObject(
                1, "2015-05-14 11:58:22.173960-06:00", Types.TIMESTAMP
        );
    }

    @Test
    public void testSetParameterMinusSixThirtyOffset() throws SQLException {
        String timeStampZeroOffset = "2015-05-14T11:58:22.173960-06:30";
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        OffsetDateTime dateTime = OffsetDateTime.parse(timeStampZeroOffset);

        subject.setParameter(mockPreparedStatement, 1, dateTime, JdbcType.TIMESTAMP);
        verify(mockPreparedStatement, times(1)).setObject(
                1, "2015-05-14 11:58:22.173960-06:30", Types.TIMESTAMP
        );
    }

    @Test
    public void testGetResultsSixDigitsFractionSeconds() throws Exception {
        String timeStampWithSixDigitFraction = "2015-05-14 11:58:22.782889-05";
        getResultsConfirmDateTimeParts(timeStampWithSixDigitFraction, ZoneOffset.of("-05"));
    }

    @Test
    public void testGetResultsFourDigitsFractionSeconds() throws Exception {
        String timeStampWithFourDigitFraction = "2015-05-14 11:58:22.6899-05";
        getResultsConfirmDateTimeParts(timeStampWithFourDigitFraction, ZoneOffset.of("-05"));
    }

    @Test
    public void testGetResultsUTCOffset() throws SQLException {
        String utcTimeStamp = "2015-05-14 11:58:22.17396+00";
        getResultsConfirmDateTimeParts(utcTimeStamp, ZoneOffset.UTC);
    }

    private void getResultsConfirmDateTimeParts(String timeStamp, ZoneOffset offset) throws SQLException {
        String columnName = "SCOPE_created_at";
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(columnName)).thenReturn(timeStamp);

        OffsetDateTime actual = subject.getResult(resultSet, columnName);
        assertThat(actual.getYear(), is(2015));
        assertThat(actual.getMonthValue(), is(5));
        assertThat(actual.getDayOfMonth(), is(14));
        assertThat(actual.getHour(), is(11));
        assertThat(actual.getMinute(), is(58));
        assertThat(actual.getSecond(), is(22));
        assertThat(actual.getOffset(), is(offset));
    }
}