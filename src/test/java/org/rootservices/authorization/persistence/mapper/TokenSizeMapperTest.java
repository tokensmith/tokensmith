package org.rootservices.authorization.persistence.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.TokenSize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 11/5/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
public class TokenSizeMapperTest {
    @Autowired
    private TokenSizeMapper subject;

    @Test
    public void getShouldBeOk() {
        TokenSize actual = subject.get();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(notNullValue()));
        assertThat(actual.getAccessTokenSize(), is(32));
        assertThat(actual.getAuthorizationCodeSize(), is(32));
        assertThat(actual.getRefreshTokenSize(), is(32));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
        assertThat(actual.getUpdatedAt(), is(notNullValue()));
    }
}