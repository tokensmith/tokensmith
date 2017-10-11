package org.rootservices.authorization.persistence.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.NonceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class NonceTypeMapperTest {

    @Autowired
    private NonceTypeMapper subject;


    @Test
    public void insertShouldInsertRecord() throws Exception {
        NonceType nonceType = new NonceType(UUID.randomUUID(), "foo", 120, OffsetDateTime.now());
        subject.insert(nonceType);

        NonceType actual = subject.getById(nonceType.getId());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(nonceType.getId()));
        assertThat(actual.getName(), is(nonceType.getName()));
        assertThat(actual.getSecondsToExpiry(), is(86400));
        assertThat(actual.getCreatedAt(), is(nonceType.getCreatedAt()));
    }


    @Test
    public void getByNameShouldBeOK() throws Exception {
        NonceType nonceType = new NonceType(UUID.randomUUID(), "foo", 120, OffsetDateTime.now());
        subject.insert(nonceType);

        NonceType actual = subject.getByName("foo");

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(nonceType.getId()));
        assertThat(actual.getName(), is(nonceType.getName()));
        assertThat(actual.getSecondsToExpiry(), is(86400));
        assertThat(actual.getCreatedAt(), is(nonceType.getCreatedAt()));
    }

}