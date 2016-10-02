package org.rootservices.authorization.persistence.entity;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
/**
 * Created by tommackenzie on 9/22/14.
 */
public class ResourceOwnerTest {

    private ResourceOwner subject;

    @Before
    public void setUp() {
        subject = new ResourceOwner();
    }

    @Test
    public void UUID() {
        UUID uuid = UUID.randomUUID();
        subject.setId(uuid);

        assertThat(subject.getId()).isEqualTo(uuid);
    }

    @Test
    public void email() {
        String email = "test@rootservices.com";
        subject.setEmail(email);

        assertThat(subject.getEmail()).isEqualTo(email);
    }

    @Test
    public void password() {
        String password = "plainTextPassword";
        subject.setPassword(password.getBytes());

        assertThat(subject.getPassword()).isEqualTo(password.getBytes());
    }

    @Test
    public void constructWithParams() {
        UUID uuid = UUID.randomUUID();
        String email = "test@rootservices.com";
        String password = "plainTextPassword";
        subject = new ResourceOwner(uuid, email, password.getBytes());

        assertThat(subject.getId()).isEqualTo(uuid);
        assertThat(subject.getEmail()).isEqualTo(email);
        assertThat(subject.getPassword()).isEqualTo(password.getBytes());
    }

}
