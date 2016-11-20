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
    public void makeWhenAuthCodeShouldHaveKeyPresent() {
        String msg =
                "### Error updating database.  Cause: org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint \"code_unique\"\n" +
                "Detail: Key (code)=(\\x243261243130246f424b7059744e4f594c57496c5a484258552f566865526546714e525a2e30333071765a6b394c324e305a4451346b714f53616657) already exists.\n" +
                "### The error may involve defaultParameterMap\n"+
                "### The error occurred while setting parameters\n"+
                "### SQL: insert into auth_code (id, code, revoked, access_request_id, expires_at)         values (             ?,             ?,             ?,             ?,             ?         )\n"+
                "### Cause: org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint \"code_unique\"\n"+
                "Detail: Key (code)=(\\x243261243130246f424b7059744e4f594c57496c5a484258552f566865526546714e525a2e30333071765a6b394c324e305a4451346b714f53616657) already exists.\n"+
                "; SQL []; ERROR: duplicate key value violates unique constraint \"code_unique\"\n"+
                "Detail: Key (code)=(\\x243261243130246f424b7059744e4f594c57496c5a484258552f566865526546714e525a2e30333071765a6b394c324e305a4451346b714f53616657) already exists.; nested exception is org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint \"code_unique\"\n"+
                "Detail: Key (code)=(\\x243261243130246f424b7059744e4f594c57496c5a484258552f566865526546714e525a2e30333071765a6b394c324e305a4451346b714f53616657) already exists.";

        DuplicateKeyException dke = new DuplicateKeyException(msg);

        DuplicateRecordException actual = subject.make(dke, "auth_code");
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getKey().isPresent(), is(true));
        assertThat(actual.getKey().get(), is("code"));
        assertThat(actual.getMessage(), is("Could not insert auth_code record. The key, code, would have caused a duplicate record."));
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