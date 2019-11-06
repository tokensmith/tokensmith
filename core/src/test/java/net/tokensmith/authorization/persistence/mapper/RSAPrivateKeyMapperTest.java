package net.tokensmith.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.TestAppConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import net.tokensmith.repository.entity.KeyUse;
import net.tokensmith.repository.entity.RSAPrivateKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 2/15/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= TestAppConfig.class, loader= AnnotationConfigContextLoader.class)
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
            assertThat(key.getUse(), is(KeyUse.SIGNATURE));
            assertThat(key.isActive(), is(true));
        }
    }

    @Test
    public void getByIdActiveSignShouldReturnKey() throws Exception {
        RSAPrivateKey rsaPrivateKey = FixtureFactory.makeRSAPrivateKey();
        subject.insert(rsaPrivateKey);

        RSAPrivateKey actual = subject.getByIdActiveSign(rsaPrivateKey.getId());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
        assertThat(actual.getUpdatedAt(), is(notNullValue()));
    }

    @Test
    public void getByIdActiveSignWhenNotActiveShouldReturnNull() throws Exception {
        RSAPrivateKey rsaPrivateKey = FixtureFactory.makeRSAPrivateKey();
        rsaPrivateKey.setActive(false);
        subject.insert(rsaPrivateKey);

        RSAPrivateKey actual = subject.getByIdActiveSign(rsaPrivateKey.getId());

        assertThat(actual, is(nullValue()));
    }

    @Test
    public void getByIdActiveSignWhenNotSignShouldReturnNull() throws Exception {
        RSAPrivateKey rsaPrivateKey = FixtureFactory.makeRSAPrivateKey();
        rsaPrivateKey.setUse(KeyUse.ENCRYPTION);
        subject.insert(rsaPrivateKey);

        RSAPrivateKey actual = subject.getByIdActiveSign(rsaPrivateKey.getId());

        assertThat(actual, is(nullValue()));
    }

    @Test
    public void getByIdActiveSignWhenNotSignNotActiveShouldReturnNull() throws Exception {
        RSAPrivateKey rsaPrivateKey = FixtureFactory.makeRSAPrivateKey();
        rsaPrivateKey.setUse(KeyUse.ENCRYPTION);
        rsaPrivateKey.setActive(false);
        subject.insert(rsaPrivateKey);

        RSAPrivateKey actual = subject.getByIdActiveSign(rsaPrivateKey.getId());

        assertThat(actual, is(nullValue()));
    }
}