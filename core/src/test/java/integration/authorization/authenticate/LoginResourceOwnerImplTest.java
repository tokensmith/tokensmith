package integration.authorization.authenticate;

import helper.fixture.FixtureFactory;
import helper.fixture.TestAppConfig;
import net.tokensmith.authorization.authenticate.LoginResourceOwner;
import net.tokensmith.authorization.authenticate.exception.UnauthorizedException;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.ResourceOwnerRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;


/**
 * Created by tommackenzie on 4/13/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=TestAppConfig.class, loader= AnnotationConfigContextLoader.class)
@Transactional
public class LoginResourceOwnerImplTest {

    @Autowired
    private ResourceOwnerRepository resourceOwnerRepository;
    @Autowired
    private LoginResourceOwner subject;

    @Test
    public void run() throws Exception {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        resourceOwnerRepository.insert(ro);

        ResourceOwner actual = subject.run(
            ro.getEmail(), "password"
        );

        assertThat(actual.getId(), is(ro.getId()));
    }

    @Test
    public void resourceOwnerNotFound() {

        ResourceOwner actual = null;
        try {
            actual = subject.run(
                "test-" + UUID.randomUUID().toString() + "@tokensmith.net", "password"
            );
        } catch (UnauthorizedException e) {
            assertThat(e.getCause(), instanceOf(RecordNotFoundException.class));
            assertThat(e.getCode(), is(ErrorCode.RESOURCE_OWNER_NOT_FOUND.getCode()));
        }

        assertThat(actual, is(nullValue()));
    }

    @Test
    public void passwordIncorrect() throws Exception {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        resourceOwnerRepository.insert(ro);

        ResourceOwner actual = null;
        try {
            actual = subject.run(
                 ro.getEmail(), "wrong-password"
            );
        } catch (UnauthorizedException e) {
            assertThat(e.getCause(), is(nullValue()));
            assertThat(e.getCode(), is(ErrorCode.PASSWORD_MISMATCH.getCode()));
        }

        assertThat(actual, is(nullValue()));
    }

}