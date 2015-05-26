package integration.security;

import helper.fixture.FixtureFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.authenticate.LoginResourceOwner;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.grant.code.constant.ErrorCode;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ResourceOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
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

        UUID actual = subject.run(
            "test@rootservices.org", "password"
        );

        assertThat(actual).isEqualTo(ro.getUuid());
    }

    @Test
    public void resourceOwnerNotFound() {

        UUID actual = null;
        try {
            actual = subject.run(
                "test@rootservices.org", "password"
            );
        } catch (UnauthorizedException e) {
            assertThat(e.getDomainCause()).isInstanceOf(RecordNotFoundException.class);
            assertThat(e.getCode()).isEqualTo(ErrorCode.RESOURCE_OWNER_NOT_FOUND.getCode());
        }

        assertThat(actual).isNull();
    }

    @Test
    public void passwordIncorrect() {
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        resourceOwnerRepository.insert(ro);

        UUID actual = null;
        try {
            actual = subject.run(
                 "test@rootservices.org", "wrong-password"
            );
        } catch (UnauthorizedException e) {
            assertThat(e.getDomainCause()).isNull();
            assertThat(e.getCode()).isEqualTo(ErrorCode.PASSWORD_MISMATCH.getCode());
        }

        assertThat(actual).isNull();
    }

}