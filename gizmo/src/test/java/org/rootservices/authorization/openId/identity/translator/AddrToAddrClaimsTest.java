package org.rootservices.authorization.openId.identity.translator;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.authorization.persistence.entity.Address;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 3/19/16.
 */
public class AddrToAddrClaimsTest {

    private AddrToAddrClaims subject;

    @Before
    public void setUp() {
        subject = new AddrToAddrClaims();
    }

    @Test
    public void toShouldTranslate() throws Exception {
        Address address = FixtureFactory.makeAddress(UUID.randomUUID());

        org.rootservices.authorization.openId.identity.entity.Address actual = subject.to(address);

        assertThat(actual, is(notNullValue()));


        assertThat(actual.getStreetAddress(), is(notNullValue()));
        assertThat(actual.getStreetAddress(), is("123 Jedi High Council Rd."));

        assertThat(actual.getRegion(), is(notNullValue()));
        assertThat(actual.getRegion(), is("Coruscant"));

        assertThat(actual.getLocality(), is(notNullValue()));
        assertThat(actual.getLocality(), is("Coruscant"));

        assertThat(actual.getPostalCode(), is(notNullValue()));
        assertThat(actual.getPostalCode(), is("12345"));

        assertThat(actual.getCountry(), is(notNullValue()));
        assertThat(actual.getCountry(), is("Old Republic"));

        assertThat(actual.getFormatted(), is(notNullValue()));

        String expectedFormatted = "123 Jedi High Council Rd.\n" +
                "Coruscant, Coruscant 12345\n" +
                "Old Republic";

        assertThat(actual.getFormatted(), is(expectedFormatted));
    }
}