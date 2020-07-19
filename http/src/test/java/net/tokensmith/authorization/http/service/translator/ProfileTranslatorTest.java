package net.tokensmith.authorization.http.service.translator;

import helpers.fixture.EntityFactory;
import helpers.fixture.ModelFactory;
import net.tokensmith.authorization.http.controller.resource.api.site.model.Name;
import net.tokensmith.authorization.http.controller.resource.api.site.model.Profile;
import net.tokensmith.repository.entity.Gender;
import net.tokensmith.repository.entity.ResourceOwner;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class ProfileTranslatorTest {
    private ProfileTranslator subject;

    @Before
    public void setUp() {
        this.subject = new ProfileTranslator();
    }

    @Test
    public void toEntityShouldBeOK() throws Exception {
        Profile from = ModelFactory.makeProfile(UUID.randomUUID());
        var actual = subject.toEntity(from);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(notNullValue()));
        assertThat(actual.getId(), is(from.getId()));
        assertThat(actual.getResourceOwnerId(), is(notNullValue()));
        assertThat(actual.getResourceOwnerId(), is(from.getResourceOwnerId()));
        assertThat(actual.getName(), is(notNullValue()));
        assertThat(actual.getName(), is(from.getName()));
        assertThat(actual.getMiddleName(), is(notNullValue()));
        assertThat(actual.getMiddleName(), is(from.getMiddleName()));
        assertThat(actual.getNickName(), is(notNullValue()));
        assertThat(actual.getNickName(), is(from.getNickName()));
        assertThat(actual.getPreferredUserName(), is(notNullValue()));
        assertThat(actual.getPreferredUserName(), is(from.getPreferredUserName()));
        assertThat(actual.getProfile(), is(notNullValue()));
        assertThat(actual.getProfile(), is(from.getProfile()));
        assertThat(actual.getPicture(), is(notNullValue()));
        assertThat(actual.getPicture(), is(from.getPicture()));
        assertThat(actual.getWebsite(), is(notNullValue()));
        assertThat(actual.getWebsite(), is(from.getWebsite()));
        assertThat(actual.getGender(), is(notNullValue()));
        assertThat(actual.getGender().isPresent(), is(true));
        assertThat(actual.getGender().get(), is(Gender.MALE));
        assertThat(actual.getBirthDate(), is(notNullValue()));
        assertThat(actual.getBirthDate(), is(from.getBirthDate()));
        assertThat(actual.getZoneInfo(), is(notNullValue()));
        assertThat(actual.getZoneInfo(), is(from.getZoneInfo()));
        assertThat(actual.getLocale(), is(notNullValue()));
        assertThat(actual.getLocale(), is(from.getLocale()));
        assertThat(actual.getPhoneNumber(), is(notNullValue()));
        assertThat(actual.getPhoneNumber(), is(from.getPhoneNumber()));
    }

    @Test
    public void toEntityNameShouldBeOK() throws Exception {
        Name from = ModelFactory.makeGivenName(UUID.randomUUID());
        var to = subject.toEntity(from);

        assertThat(to, is(notNullValue()));
        assertThat(to.getId(), is(notNullValue()));
        assertThat(to.getId(), is(from.getId()));
        assertThat(to.getResourceOwnerProfileId(), is(notNullValue()));
        assertThat(to.getResourceOwnerProfileId(), is(from.getProfileId()));
        assertThat(to.getName(), is(notNullValue()));
        assertThat(to.getName(), is(from.getName()));
    }

    @Test
    public void toModelShouldBeOK() throws Exception {
        ResourceOwner from = EntityFactory.makeResourceOwnerWithProfile();
        var actual = subject.toModel(from);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(notNullValue()));
        assertThat(actual.getId(), is(from.getProfile().getId()));
        assertThat(actual.getResourceOwnerId(), is(notNullValue()));
        assertThat(actual.getResourceOwnerId(), is(from.getId()));
        assertThat(actual.getName(), is(notNullValue()));
        assertThat(actual.getName(), is(from.getProfile().getName()));
        assertThat(actual.getMiddleName(), is(notNullValue()));
        assertThat(actual.getMiddleName(), is(from.getProfile().getMiddleName()));
        assertThat(actual.getNickName(), is(notNullValue()));
        assertThat(actual.getNickName(), is(from.getProfile().getNickName()));
        assertThat(actual.getPreferredUserName(), is(notNullValue()));
        assertThat(actual.getPreferredUserName(), is(from.getProfile().getPreferredUserName()));
        assertThat(actual.getProfile(), is(notNullValue()));
        assertThat(actual.getProfile(), is(from.getProfile().getProfile()));
        assertThat(actual.getPicture(), is(notNullValue()));
        assertThat(actual.getPicture(), is(from.getProfile().getPicture()));
        assertThat(actual.getWebsite(), is(notNullValue()));
        assertThat(actual.getWebsite(), is(from.getProfile().getWebsite()));
        assertThat(actual.getGender(), is(notNullValue()));
        assertThat(actual.getGender().isPresent(), is(true));
        assertThat(actual.getGender().get(), is(Gender.MALE.toString().toLowerCase()));
        assertThat(actual.getBirthDate(), is(notNullValue()));
        assertThat(actual.getBirthDate(), is(from.getProfile().getBirthDate()));
        assertThat(actual.getZoneInfo(), is(notNullValue()));
        assertThat(actual.getZoneInfo(), is(from.getProfile().getZoneInfo()));
        assertThat(actual.getLocale(), is(notNullValue()));
        assertThat(actual.getLocale(), is(from.getProfile().getLocale()));
        assertThat(actual.getPhoneNumber(), is(notNullValue()));
        assertThat(actual.getPhoneNumber(), is(from.getProfile().getPhoneNumber()));

        assertThat(actual.getGivenName(), is(notNullValue()));
        assertThat(actual.getGivenName().getId(), is(from.getProfile().getGivenNames().get(0).getId()));
        assertThat(actual.getGivenName().getName(), is(from.getProfile().getGivenNames().get(0).getName()));
        assertThat(actual.getGivenName().getProfileId(), is(from.getProfile().getGivenNames().get(0).getResourceOwnerProfileId()));

        assertThat(actual.getFamilyName(), is(notNullValue()));
        assertThat(actual.getFamilyName().getId(), is(from.getProfile().getFamilyNames().get(0).getId()));
        assertThat(actual.getFamilyName().getName(), is(from.getProfile().getFamilyNames().get(0).getName()));
        assertThat(actual.getFamilyName().getProfileId(), is(from.getProfile().getFamilyNames().get(0).getResourceOwnerProfileId()));
    }

}