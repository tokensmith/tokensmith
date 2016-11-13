package org.rootservices.authorization.persistence.factory;

import org.junit.Before;
import org.junit.Test;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.springframework.dao.DuplicateKeyException;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 11/11/16.
 */
public class DuplicateRecordExceptionFactoryTest {
    private DuplicateRecordExceptionFactory subject;

    @Before
    public void setUp() {
        subject = new DuplicateRecordExceptionFactory();
    }

    @Test
    public void makeShouldHaveKeyPresent() {
        String msg =
            "### Error updating database.  Cause: org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint \"token_unique\"\n" +
            "Detail: Key (token)=(\\x243261243130246f424b7059744e4f594c57496c5a484258552f566865497843673249577244476d474b504b4e316e59614378657a466b342e314b69) already exists.\n" +
            "### The error may involve defaultParameterMap\n" +
            "### The error occurred while setting parameters\n" +
            "### SQL: insert into token (id, token, grant_type, expires_at)         values (             ?,             ?,             ?,             ?         )\n" +
            "### Cause: org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint \"token_unique\"\n" +
            "Detail: Key (token)=(\\x243261243130246f424b7059744e4f594c57496c5a484258552f566865497843673249577244476d474b504b4e316e59614378657a466b342e314b69) already exists.\n" +
            "; SQL []; ERROR: duplicate key value violates unique constraint \"token_unique\"\n" +
            "Detail: Key (token)=(\\x243261243130246f424b7059744e4f594c57496c5a484258552f566865497843673249577244476d474b504b4e316e59614378657a466b342e314b69) already exists.; nested exception is org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint \"token_unique\"\n" +
            "Detail: Key (token)=(\\x243261243130246f424b7059744e4f594c57496c5a484258552f566865497843673249577244476d474b504b4e316e59614378657a466b342e314b69) already exists.\"";

        DuplicateKeyException dke = new DuplicateKeyException(msg);

        DuplicateRecordException actual = subject.make(dke, "token");
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getKey().isPresent(), is(true));
        assertThat(actual.getKey().get(), is("token"));
        assertThat(actual.getMessage(), is("Could not insert token record. The key, token, would have caused a duplicate record."));
    }

    @Test
    public void makeShouldNotHaveKeyPresent() {
        String msg = "foo";

        DuplicateKeyException dke = new DuplicateKeyException(msg);

        DuplicateRecordException actual = subject.make(dke, "token");
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getKey().isPresent(), is(false));
        assertThat(actual.getMessage(), is("Could not insert token record. Unable to determine the key that would have caused the duplicate record."));
    }

}