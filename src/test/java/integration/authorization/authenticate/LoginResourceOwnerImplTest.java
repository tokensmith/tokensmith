package integration.authorization.authenticate;

import helper.fixture.FixtureFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.authenticate.LoginResourceOwner;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ResourceOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by tommackenzie on 4/13/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-auth-test.xml")
@Transactional
public class LoginResourceOwnerImplTest {

    @Autowired
    private ResourceOwnerRepository resourceOwnerRepository;
    @Autowired
    private LoginResourceOwner subject;

    @Test
    public void run() throws UnauthorizedException {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        resourceOwnerRepository.insert(ro);

        ResourceOwner actual = subject.run(
            ro.getEmail(), "password"
        );

        assertThat(actual.getId()).isEqualTo(ro.getId());
    }

    @Test
    public void resourceOwnerNotFound() {

        ResourceOwner actual = null;
        try {
            actual = subject.run(
                "test-" + UUID.randomUUID().toString() + "@rootservices.org", "password"
            );
        } catch (UnauthorizedException e) {
            assertThat(e.getCause()).isInstanceOf(RecordNotFoundException.class);
            assertThat(e.getCode()).isEqualTo(ErrorCode.RESOURCE_OWNER_NOT_FOUND.getCode());
        }

        assertThat(actual).isNull();
    }

    @Test
    public void passwordIncorrect() {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        resourceOwnerRepository.insert(ro);

        ResourceOwner actual = null;
        try {
            actual = subject.run(
                 ro.getEmail(), "wrong-password"
            );
        } catch (UnauthorizedException e) {
            assertThat(e.getCause()).isNull();
            assertThat(e.getCode()).isEqualTo(ErrorCode.PASSWORD_MISMATCH.getCode());
        }

        assertThat(actual).isNull();
    }

}