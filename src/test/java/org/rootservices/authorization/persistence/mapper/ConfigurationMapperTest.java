package org.rootservices.authorization.persistence.mapper;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 11/5/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class ConfigurationMapperTest {
    @Autowired
    private ConfigurationMapper subject;

    @Test
    public void getShouldBeOk() {
        Configuration actual = subject.get();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(notNullValue()));
        assertThat(actual.getAccessTokenSize(), is(32));
        assertThat(actual.getAuthorizationCodeSize(), is(32));
        assertThat(actual.getRefreshTokenSize(), is(32));

        assertThat(actual.getAccessTokenClientSecondsToExpiry(), is(3600L));
        assertThat(actual.getAccessTokenCodeSecondsToExpiry(), is(3600L));
        assertThat(actual.getAccessTokenPasswordSecondsToExpiry(), is(3600L));
        assertThat(actual.getAccessTokenRefreshSecondsToExpiry(), is(3600L));
        assertThat(actual.getAccessTokenTokenSecondsToExpiry(), is(3600L));
        assertThat(actual.getAuthorizationCodeSecondsToExpiry(), is(120L));
        assertThat(actual.getRefreshTokenSecondsToExpiry(), is(1209600L));

        assertThat(actual.getCreatedAt(), is(notNullValue()));
        assertThat(actual.getUpdatedAt(), is(notNullValue()));
    }

    @Test
    @Ignore
    public void updateAccessTokenSizeShouldBeOk() {
        Configuration original = subject.get();

        subject.updateAccessTokenSize(original.getId(), 50);

        Configuration actual = subject.get();
        assertThat(actual.getAccessTokenSize(), is(50));
        assertThat(actual.getAuthorizationCodeSize(), is(original.getAuthorizationCodeSize()));
        assertThat(actual.getRefreshTokenSize(), is(original.getRefreshTokenSize()));
        assertThat(actual.getUpdatedAt().compareTo(original.getUpdatedAt()), is(1));
    }

    @Test
    @Ignore
    public void updateAuthorizationCodeSizeShouldBeOk() {
        Configuration original = subject.get();

        subject.updateAuthorizationCodeSize(original.getId(), 50);

        Configuration actual = subject.get();
        assertThat(actual.getAuthorizationCodeSize(), is(50));
        assertThat(actual.getAccessTokenSize(), is(original.getAccessTokenSize()));
        assertThat(actual.getRefreshTokenSize(), is(original.getRefreshTokenSize()));
        assertThat(actual.getUpdatedAt().compareTo(original.getUpdatedAt()), is(1));
    }

    @Test
    @Ignore
    public void updateRefreshTokenSizeShouldBeOk() {
        Configuration original = subject.get();

        subject.updateRefreshTokenSize(original.getId(), 50);

        Configuration actual = subject.get();
        assertThat(actual.getRefreshTokenSize(), is(50));
        assertThat(actual.getAccessTokenSize(), is(original.getAccessTokenSize()));
        assertThat(actual.getAuthorizationCodeSize(), is(original.getAuthorizationCodeSize()));
        assertThat(actual.getUpdatedAt().compareTo(original.getUpdatedAt()), is(1));
    }
}