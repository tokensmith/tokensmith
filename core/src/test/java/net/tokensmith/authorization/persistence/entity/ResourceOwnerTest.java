package net.tokensmith.authorization.persistence.entity;

import net.tokensmith.repository.entity.ResourceOwner;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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

        assertThat(subject.getId(), is(uuid));
    }

    @Test
    public void email() {
        String email = "test@tokensmith.net";
        subject.setEmail(email);

        assertThat(subject.getEmail(), is(email));
    }

    @Test
    public void password() {
        String password = "plainTextPassword";
        subject.setPassword(password);

        assertThat(subject.getPassword(), is(password));
    }

    @Test
    public void constructWithParams() {
        UUID uuid = UUID.randomUUID();
        String email = "test@tokensmith.net";
        String password = "plainTextPassword";
        subject = new ResourceOwner(uuid, email, password);

        assertThat(subject.getId(), is(uuid));
        assertThat(subject.getEmail(), is(email));
        assertThat(subject.getPassword(), is(password));
    }

}
