package net.tokensmith.authorization.http.service.translator;

import helpers.fixture.ModelFactory;
import net.tokensmith.authorization.http.controller.resource.api.site.model.Address;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class AddressTranslatorTest {
    private AddressTranslator subject;

    @Before
    public void setUp() throws Exception {
        this.subject = new AddressTranslator();
    }

    @Test
    public void toShouldBeOk() {
        Address from = ModelFactory.makeAddress();
        var actual = subject.toEntity(from);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(notNullValue()));
        assertThat(actual.getId(), is(from.getId()));
        assertThat(actual.getProfileId(), is(notNullValue()));
        assertThat(actual.getProfileId(), is(from.getProfileId()));
        assertThat(actual.getStreetAddress(), is(notNullValue()));
        assertThat(actual.getStreetAddress(), is(from.getStreetAddress()));
        assertThat(actual.getStreetAddress2(), is(notNullValue()));
        assertThat(actual.getStreetAddress2().isPresent(), is(false));
        assertThat(actual.getLocality(), is(notNullValue()));
        assertThat(actual.getLocality(), is(from.getLocality()));
        assertThat(actual.getRegion(), is(notNullValue()));
        assertThat(actual.getRegion(), is(from.getLocality()));
        assertThat(actual.getPostalCode(), is(notNullValue()));
        assertThat(actual.getPostalCode(), is(from.getPostalCode()));
        assertThat(actual.getCountry(), is(notNullValue()));
        assertThat(actual.getCountry(), is(from.getCountry()));
    }
}