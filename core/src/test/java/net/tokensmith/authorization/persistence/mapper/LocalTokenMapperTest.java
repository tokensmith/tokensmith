package net.tokensmith.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.TestAppConfig;
import net.tokensmith.repository.entity.LocalToken;
import net.tokensmith.repository.entity.ResourceOwner;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= TestAppConfig.class, loader= AnnotationConfigContextLoader.class)
@Transactional
public class LocalTokenMapperTest {
    @Autowired
    private ResourceOwnerMapper resourceOwnerMapper;

    @Autowired
    private LocalTokenMapper subject;

    @Test
    public void insert() {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        resourceOwnerMapper.insert(ro);

        LocalToken localToken = new LocalToken.Builder()
            .id(UUID.randomUUID())
            .revoked(false)
            .token( "local-token")
            .resourceOwnerId(ro.getId())
            .expiresAt(OffsetDateTime.now().plusDays(1))
            .createdAt(OffsetDateTime.now())
            .build();

        subject.insert(localToken);

        assertThat(localToken.getCreatedAt(), is(notNullValue()));
    }

    @Test
    public void insertDuplicateShouldThrowDuplicateKeyException() {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        resourceOwnerMapper.insert(ro);

        LocalToken localToken = new LocalToken.Builder()
                .id(UUID.randomUUID())
                .token( "local-token")
                .revoked(false)
                .resourceOwnerId(ro.getId())
                .expiresAt(OffsetDateTime.now().plusDays(1))
                .createdAt(OffsetDateTime.now())
                .build();

        // first
        subject.insert(localToken);

        LocalToken duplicateLocalToken = new LocalToken.Builder()
                .id(UUID.randomUUID())
                .token( "local-token")
                .revoked(false)
                .resourceOwnerId(ro.getId())
                .expiresAt(OffsetDateTime.now().plusDays(1))
                .createdAt(OffsetDateTime.now())
                .build();

        DuplicateKeyException actual = null;
        try {
            subject.insert(duplicateLocalToken);
        } catch (DuplicateKeyException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage().contains("Detail: Key (active_token)"), Is.is(true));
    }

    @Test
    public void getById() {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        resourceOwnerMapper.insert(ro);

        LocalToken localToken = new LocalToken.Builder()
                .id(UUID.randomUUID())
                .token( "local-token")
                .revoked(false)
                .resourceOwnerId(ro.getId())
                .expiresAt(OffsetDateTime.now().plusDays(1))
                .createdAt(OffsetDateTime.now())
                .build();

        subject.insert(localToken);

        LocalToken actual = subject.getById(localToken.getId());

        assertThat(actual.getId(), is(localToken.getId()));
        assertThat(actual.getToken(), is(localToken.getToken()));
        assertThat(actual.isRevoked(), is(false));
        assertThat(actual.getResourceOwnerId(), is(localToken.getResourceOwnerId()));
        assertThat(actual.getExpiresAt().toEpochSecond(), is(localToken.getExpiresAt().toEpochSecond()));
        assertThat(actual.getCreatedAt().toEpochSecond(), is(localToken.getCreatedAt().toEpochSecond()));
    }

    @Test
    public void revokeActive() {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        resourceOwnerMapper.insert(ro);

        LocalToken localToken = new LocalToken.Builder()
                .id(UUID.randomUUID())
                .token( "local-token")
                .revoked(false)
                .resourceOwnerId(ro.getId())
                .expiresAt(OffsetDateTime.now().plusDays(1))
                .createdAt(OffsetDateTime.now())
                .build();

        subject.insert(localToken);

        subject.revokeActive(ro.getId());

        LocalToken actual = subject.getById(localToken.getId());

        assertThat(actual.isRevoked(), is(true));
    }
}