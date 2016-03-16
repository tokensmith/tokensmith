package org.rootservices.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.Address;
import org.rootservices.authorization.persistence.entity.Profile;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 3/12/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class AddressMapperTest {

    @Autowired
    private ResourceOwnerMapper resourceOwnerMapper;

    @Autowired
    private ProfileMapper profileMapper;

    @Autowired
    private AddressMapper subject;


    @Test
    public void insertShouldInsertProfile() throws URISyntaxException {

        Address address = FixtureFactory.makeAddress();

        subject.insert(address);
    }

    @Test
    public void getByIdShouldGetAddress() throws URISyntaxException {

        Address address = FixtureFactory.makeAddress();
        subject.insert(address);

        Address actual = subject.getById(address.getId());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(notNullValue()));

        assertThat(actual.getStreetAddress(), is("123 Jedi High Council Rd."));
        assertThat(actual.getStreetAddress2().isPresent(), is(false));
        assertThat(actual.getLocality(), is("Coruscant"));
        assertThat(actual.getRegion(), is("Coruscant"));
        assertThat(actual.getPostalCode(), is("12345"));
        assertThat(actual.getCountry(), is("Old Republic"));
        assertThat(actual.getUpdatedAt(), is(notNullValue()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
    }
}