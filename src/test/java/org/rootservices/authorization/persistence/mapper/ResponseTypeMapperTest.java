package org.rootservices.authorization.persistence.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 8/9/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class ResponseTypeMapperTest {
    @Autowired
    private ResponseTypeMapper subject;

    @Test
    public void getByNameShouldBeOk() {
        ResponseType responseType = subject.getByName("CODE");

        assertThat(responseType, is(notNullValue()));
        assertThat(responseType.getId(), is(notNullValue()));
        assertThat(responseType.getName(), is("CODE"));
        assertThat(responseType.getCreatedAt(), is(notNullValue()));
        assertThat(responseType.getUpdatedAt(), is(notNullValue()));
    }
}