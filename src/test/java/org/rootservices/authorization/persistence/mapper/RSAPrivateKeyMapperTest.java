package org.rootservices.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.PrivateKeyUse;
import org.rootservices.authorization.persistence.entity.RSAPrivateKey;
import org.rootservices.jwt.entity.jwk.Use;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 2/15/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class RSAPrivateKeyMapperTest {

    @Autowired
    private RSAPrivateKeyMapper subject;

    @Test
    public void insert() {
        RSAPrivateKey rsaPrivateKey = FixtureFactory.makeRSAPrivateKey();
        subject.insert(rsaPrivateKey);

        RSAPrivateKey actual = subject.getById(rsaPrivateKey.getId());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
        assertThat(actual.getUpdatedAt(), is(notNullValue()));
    }

    @Test
    public void getMostRecentAndActiveForSigningShouldFindRecord() {
        RSAPrivateKey rsaPrivateKeyA = FixtureFactory.makeRSAPrivateKey();
        subject.insert(rsaPrivateKeyA);
        rsaPrivateKeyA = subject.getById(rsaPrivateKeyA.getId());

        RSAPrivateKey rsaPrivateKeyB = FixtureFactory.makeRSAPrivateKey();

        // make sure B is the latest.
        rsaPrivateKeyB.setCreatedAt(rsaPrivateKeyA.getCreatedAt().plusSeconds(1));
        rsaPrivateKeyB.setUpdatedAt(rsaPrivateKeyA.getUpdatedAt().plusSeconds(1));
        subject.insertWithDateTimeValues(rsaPrivateKeyB);

        RSAPrivateKey actual = subject.getMostRecentAndActiveForSigning();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(rsaPrivateKeyB.getId()));
        assertThat(actual.getUse(), is(rsaPrivateKeyB.getUse()));

        assertThat(actual.getModulus(), is(rsaPrivateKeyB.getModulus()));
        assertThat(actual.getPublicExponent(), is(rsaPrivateKeyB.getPublicExponent()));
        assertThat(actual.getPrivateExponent(), is(rsaPrivateKeyB.getPrivateExponent()));
        assertThat(actual.getPrimeP(), is(rsaPrivateKeyB.getPrimeP()));
        assertThat(actual.getPrimeQ(), is(rsaPrivateKeyB.getPrimeQ()));
        assertThat(actual.getPrimeExponentP(), is(rsaPrivateKeyB.getPrimeExponentP()));
        assertThat(actual.getPrimeExponentQ(), is(rsaPrivateKeyB.getPrimeExponentQ()));
        assertThat(actual.getCrtCoefficient(), is(rsaPrivateKeyB.getCrtCoefficient()));

        assertThat(actual.isActive(), is(true));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
        assertThat(actual.getUpdatedAt(), is(notNullValue()));
    }

    @Test
    public void getWhereActiveAndUseIsSignShouldReturnMany() throws Exception {
        for(int i=0; i<10; i++) {
            RSAPrivateKey rsaPrivateKey = FixtureFactory.makeRSAPrivateKey();
            subject.insert(rsaPrivateKey);
        }

        List<RSAPrivateKey> actual = subject.getWhereActiveAndUseIsSign(10, 0);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(10));

        for(RSAPrivateKey key: actual) {
            assertThat(key.getUse(), is(PrivateKeyUse.SIGNATURE));
            assertThat(key.isActive(), is(true));
        }
    }

}