package net.tokensmith.authorization.persistence.mapper;

import helper.fixture.TestAppConfig;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import net.tokensmith.repository.entity.Nonce;
import net.tokensmith.repository.entity.NonceType;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= TestAppConfig.class, loader= AnnotationConfigContextLoader.class)
@Transactional
public class NonceMapperTest {

    @Autowired
    private RandomString randomString;

    @Autowired
    private NonceTypeMapper nonceTypeMapper;

    @Autowired
    private ResourceOwnerMapper resourceOwnerMapper;

    @Autowired
    private NonceMapper subject;

    public Nonce insertNonce(String type, String nonceValue) {

        NonceType nonceType = nonceTypeMapper.getByName(type);

        if (nonceType == null) {
            nonceType = new NonceType(UUID.randomUUID(), type, 120, OffsetDateTime.now());
            nonceTypeMapper.insert(nonceType);
        }

        String password = "plainTextPassword";
        ResourceOwner user = new ResourceOwner(UUID.randomUUID(), UUID.randomUUID() + "@rootservices.com", password);

        resourceOwnerMapper.insert(user);

        Nonce nonce = new Nonce();
        nonce.setId(UUID.randomUUID());
        nonce.setNonceType(nonceType);
        nonce.setResourceOwner(user);
        nonce.setCreatedAt(OffsetDateTime.now());
        nonce.setExpiresAt(OffsetDateTime.now().plusMinutes(10));
        nonce.setNonce(nonceValue);

        subject.insert(nonce);

        return nonce;
    }

    @Test
    public void insert() throws Exception {
        String nonceValue = randomString.run();
        Nonce nonce = insertNonce("foo", nonceValue);

        Nonce actual = subject.getById(nonce.getId());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(nonce.getId()));
        assertThat(actual.getNonce(), is(nonceValue));
        assertThat(actual.getRevoked(), is(false));
        assertThat(actual.getSpent(), is(false));
        assertThat(actual.getExpiresAt(), is(nonce.getExpiresAt()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));

        Assert.assertThat(actual.getNonceType().getId(), is(nonce.getNonceType().getId()));
        Assert.assertThat(actual.getNonceType().getName(), is(nonce.getNonceType().getName()));
        Assert.assertThat(actual.getNonceType().getSecondsToExpiry(), is(86400));
        Assert.assertThat(actual.getNonceType().getCreatedAt(), is(notNullValue()));

        assertThat(actual.getResourceOwner().getId(), is(nonce.getResourceOwner().getId()));
        assertThat(actual.getResourceOwner().getEmail(), is(nonce.getResourceOwner().getEmail()));
        assertThat(actual.getResourceOwner().getPassword(), is(nonce.getResourceOwner().getPassword()));
        assertThat(actual.getResourceOwner().isEmailVerified(), is(false));
        assertThat(actual.getResourceOwner().getCreatedAt(), is(CoreMatchers.notNullValue()));
    }


    @Test
    public void getByTypeAndNonceShouldReturnRecord() throws Exception {
        String nonceValue = randomString.run();
        Nonce nonce = insertNonce("bar", nonceValue);

        Nonce actual = subject.getByTypeAndNonce("bar", nonceValue);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(nonce.getId()));
        assertThat(actual.getNonce(), is(nonceValue));
        assertThat(actual.getRevoked(), is(false));
        assertThat(actual.getSpent(), is(false));
        assertThat(actual.getExpiresAt(), is(nonce.getExpiresAt()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));

        Assert.assertThat(actual.getNonceType().getId(), is(nonce.getNonceType().getId()));
        Assert.assertThat(actual.getNonceType().getName(), is(nonce.getNonceType().getName()));
        Assert.assertThat(actual.getNonceType().getSecondsToExpiry(), is(86400));
        Assert.assertThat(actual.getNonceType().getCreatedAt(), is(notNullValue()));

        assertThat(actual.getResourceOwner().getId(), is(nonce.getResourceOwner().getId()));
        assertThat(actual.getResourceOwner().getEmail(), is(nonce.getResourceOwner().getEmail()));
        assertThat(actual.getResourceOwner().getPassword(), is(nonce.getResourceOwner().getPassword()));
        assertThat(actual.getResourceOwner().isEmailVerified(), is(false));
        assertThat(actual.getResourceOwner().getCreatedAt(), is(CoreMatchers.notNullValue()));
    }

    @Test
    public void getByNonceShouldReturnRecord() throws Exception {
        String nonceValue = randomString.run();
        Nonce nonce = insertNonce("bar", nonceValue);

        Nonce actual = subject.getByNonce(nonceValue);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(nonce.getId()));
        assertThat(actual.getNonce(), is(nonceValue));
        assertThat(actual.getRevoked(), is(false));
        assertThat(actual.getSpent(), is(false));
        assertThat(actual.getExpiresAt(), is(nonce.getExpiresAt()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));

        Assert.assertThat(actual.getNonceType().getId(), is(nonce.getNonceType().getId()));
        Assert.assertThat(actual.getNonceType().getName(), is(nonce.getNonceType().getName()));
        Assert.assertThat(actual.getNonceType().getSecondsToExpiry(), is(86400));
        Assert.assertThat(actual.getNonceType().getCreatedAt(), is(notNullValue()));

        assertThat(actual.getResourceOwner().getId(), is(nonce.getResourceOwner().getId()));
        assertThat(actual.getResourceOwner().getEmail(), is(nonce.getResourceOwner().getEmail()));
        assertThat(actual.getResourceOwner().getPassword(), is(nonce.getResourceOwner().getPassword()));
        assertThat(actual.getResourceOwner().isEmailVerified(), is(false));
        assertThat(actual.getResourceOwner().getCreatedAt(), is(CoreMatchers.notNullValue()));
    }

    @Test
    public void revokeUnSpent() {
        String nonceValue = randomString.run();
        Nonce nonceToNotRevoke = insertNonce("foo", nonceValue);

        nonceValue = randomString.run();
        Nonce nonceToRevoke = insertNonce("foo", nonceValue);
        nonceToRevoke = subject.getById(nonceToRevoke.getId());

        // should not be spent..
        assertThat(nonceToRevoke.getSpent(), is(false));
        assertThat(nonceToRevoke.getRevoked(), is(false));

        subject.revokeUnSpent("foo", nonceToRevoke.getResourceOwner().getId());

        // should revoke the nonce
        Nonce actual = subject.getById(nonceToRevoke.getId());
        assertThat(actual.getSpent(), is(false));
        assertThat(actual.getRevoked(), is(true));

        // should NOT have revoked..
        Nonce actualNotRevoked = subject.getById(nonceToNotRevoke.getId());
        assertThat(actualNotRevoked.getSpent(), is(false));
        assertThat(actualNotRevoked.getRevoked(), is(false));
    }


    @Test
    public void setSpent() {
        String nonceValue = randomString.run();
        Nonce nonce = insertNonce("foo", nonceValue);
        subject.setSpent(nonce.getId());

        Nonce actual = subject.getById(nonce.getId());

        assertThat(actual.getSpent(), is(true));
    }

}