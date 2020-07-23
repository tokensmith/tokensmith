package net.tokensmith.authorization.http.controller.resource.html.between;

import net.tokensmith.authorization.http.controller.security.WebSiteSession;
import net.tokensmith.authorization.http.controller.security.WebSiteUser;
import net.tokensmith.authorization.http.presenter.AssetPresenter;
import net.tokensmith.authorization.security.ciphers.HashToken;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.exception.HaltException;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.ResourceOwnerRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class WebSiteAuthRequiredTest {
    private WebSiteAuthRequired subject;

    @Mock
    private HashToken mockHashToken;
    @Mock
    private ResourceOwnerRepository mockResourceOwnerRepository;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new WebSiteAuthRequired(mockHashToken, mockResourceOwnerRepository, "/assets/css/global.css");
    }

    @Test
    public void processShouldFindUser() throws Exception {
        WebSiteSession session = new WebSiteSession("local-token", OffsetDateTime.now().toEpochSecond());
        Request<WebSiteSession, WebSiteUser> req = new Request<>();
        req.setSession(Optional.of(session));

        Response<WebSiteSession> res = new Response<WebSiteSession>();

        ResourceOwner resourceOwner = new ResourceOwner();
        resourceOwner.setId(UUID.randomUUID());
        resourceOwner.setEmail("obi-wan@tokensmith.net");
        resourceOwner.setEmailVerified(true);
        resourceOwner.setCreatedAt(OffsetDateTime.now());

        when(mockHashToken.run(session.getToken())).thenReturn("hashed-token");
        when(mockResourceOwnerRepository.getByLocalToken(eq("hashed-token"))).thenReturn(resourceOwner);

        subject.process(Method.GET, req, res);

        assertThat(req.getUser().isPresent(), is(true));
        WebSiteUser user = req.getUser().get();

        assertThat(user.getId(), is(resourceOwner.getId()));
        assertThat(user.getEmail(), is(resourceOwner.getEmail()));
        assertThat(user.isEmailVerified(), is(resourceOwner.isEmailVerified()));
        assertThat(user.getCreatedAt(), is(resourceOwner.getCreatedAt()));
    }

    @Test
    public void processWhenUserNotFoundShouldReturn403() throws Exception {
        WebSiteSession session = new WebSiteSession("local-token", OffsetDateTime.now().toEpochSecond());
        Request<WebSiteSession, WebSiteUser> req = new Request<>();
        req.setSession(Optional.of(session));

        Response<WebSiteSession> res = new Response<WebSiteSession>();
        res.setTemplate(Optional.empty());

        when(mockHashToken.run(session.getToken())).thenReturn("hashed-token");
        RecordNotFoundException rnfe = new RecordNotFoundException();
        doThrow(rnfe).when(mockResourceOwnerRepository).getByLocalToken(eq("hashed-token"));

        HaltException actual = null;
        try {
            subject.process(Method.GET, req, res);
        } catch(HaltException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(res.getStatusCode(), is(StatusCode.UNAUTHORIZED));
        assertThat(res.getTemplate().isPresent(), is(true));
        assertTrue(res.getPresenter().isPresent());
        AssetPresenter presenter = (AssetPresenter) res.getPresenter().get();
        assertThat(presenter.getGlobalCssPath(), is("/assets/css/global.css"));
    }

    @Test
    public void processWhenNoSessionShouldReturn403() throws Exception {
        Request<WebSiteSession, WebSiteUser> req = new Request<>();
        req.setSession(Optional.empty());

        Response<WebSiteSession> res = new Response<>();
        res.setTemplate(Optional.empty());

        HaltException actual = null;
        try {
            subject.process(Method.GET, req, res);
        } catch(HaltException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(res.getStatusCode(), is(StatusCode.UNAUTHORIZED));
        assertThat(res.getTemplate().isPresent(), is(true));
        assertTrue(res.getPresenter().isPresent());
        AssetPresenter presenter = (AssetPresenter) res.getPresenter().get();
        assertThat(presenter.getGlobalCssPath(), is("/assets/css/global.css"));
    }
}