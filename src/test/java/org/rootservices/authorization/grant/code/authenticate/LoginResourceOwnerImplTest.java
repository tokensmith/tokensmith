package org.rootservices.authorization.grant.code.authenticate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.grant.code.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.grant.code.constant.ErrorCode;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ResourceOwnerRepository;
import org.rootservices.authorization.security.Hash;

import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 4/13/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class LoginResourceOwnerImplTest {

    @Mock
    private Hash mockHash;
    @Mock
    private ResourceOwnerRepository mockResourceOwnerRepository;

    private LoginResourceOwner subject;

    @Before
    public void setUp() {
        subject = new LoginResourceOwnerImpl(mockHash, mockResourceOwnerRepository);
    }

    @Test
    public void run() throws UnauthorizedException, RecordNotFoundException {
        String userName = "test@rootservices.org";
        String plainTextPassword = "plainTextPassword";
        String hashedPassword = "hashedPassword";

        ResourceOwner resourceOwner = new ResourceOwner();
        resourceOwner.setUuid(UUID.randomUUID());

        when(mockHash.run(plainTextPassword)).thenReturn(hashedPassword);
        when(mockResourceOwnerRepository.getByEmailAndPassword(userName, hashedPassword.getBytes())).thenReturn(resourceOwner);

        UUID actual = subject.run(userName, plainTextPassword);

        assertThat(actual).isEqualTo(resourceOwner.getUuid());
    }

    @Test
    public void unauthorized() throws RecordNotFoundException {
        String userName = "test@rootservices.org";
        String plainTextPassword = "plainTextPassword";
        String hashedPassword = "hashedPassword";

        when(mockHash.run(plainTextPassword)).thenReturn(hashedPassword);
        when(mockResourceOwnerRepository.getByEmailAndPassword(userName, hashedPassword.getBytes())).thenThrow(RecordNotFoundException.class);

        UUID actual = null;
        try {
            actual = subject.run(userName, plainTextPassword);
        } catch (UnauthorizedException e) {
            assertThat(e.getDomainCause()).isInstanceOf(RecordNotFoundException.class);
            assertThat(e.getCode()).isEqualTo(ErrorCode.UNAUTHORIZED.getCode());
        }

        assertThat(actual).isNull();
    }
}