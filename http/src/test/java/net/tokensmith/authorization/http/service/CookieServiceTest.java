package net.tokensmith.authorization.http.service;

import net.tokensmith.authorization.http.controller.resource.html.CookieName;
import net.tokensmith.authorization.http.controller.resource.html.authorization.claim.RedirectClaim;
import net.tokensmith.authorization.http.controller.security.WebSiteSession;
import net.tokensmith.authorization.http.presenter.AuthorizationPresenter;
import net.tokensmith.otter.config.CookieConfig;
import net.tokensmith.otter.controller.entity.Cookie;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.controller.header.Header;
import net.tokensmith.otter.security.cookie.CookieJwtException;
import net.tokensmith.otter.security.cookie.CookieSecurity;
import net.tokensmith.otter.security.cookie.either.CookieError;
import net.tokensmith.otter.security.cookie.either.ReadEither;
import net.tokensmith.otter.security.cookie.either.ReadError;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Optional;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CookieServiceTest {
    private CookieService subject;
    private CookieConfig redirectCookieConfig;
    @Mock
    private CookieSecurity mockCookieSigner;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        redirectCookieConfig = new CookieConfig.Builder()
            .name(CookieName.REDIRECT.toString())
            .httpOnly(true)
            .secure(true)
            .age(-1)
            .build();

        subject = new CookieService(mockCookieSigner, redirectCookieConfig);

    }

    @Test
    public void addRedirectForAuthShouldAddCookie() throws Exception {

        Cookie redirectCookie = new Cookie.Builder()
                .name(CookieName.REDIRECT.toString())
                .build();

        ArgumentCaptor<CookieConfig> cookieConfig = ArgumentCaptor.forClass(CookieConfig.class);
        ArgumentCaptor<RedirectClaim> redirectCapture = ArgumentCaptor.forClass(RedirectClaim.class);

        when(mockCookieSigner.make(
                cookieConfig.capture(),
                redirectCapture.capture())
        ).thenReturn(redirectCookie);

        Response<WebSiteSession> response = new Response<>();
        response.setCookies(new HashMap<>());

        String path = "/authorization?foo";
        subject.addRedirectForAuth(path, response);

        assertThat(response.getCookies(), is(notNullValue()));
        assertThat(response.getCookies().get(CookieName.REDIRECT.toString()), is(redirectCookie));

        // the claims to make cookie should have correct values
        assertThat(redirectCapture.getValue().getRedirect(), is(path));
        assertThat(redirectCapture.getValue().getDone(), is(false));

        // should use correct config to make the redirect cookie
        assertThat(cookieConfig.getValue(), is(redirectCookieConfig));

    }

    @Test
    public void addRedirectForAuthShouldNotAddCookie() throws Exception {

        ArgumentCaptor<CookieConfig> cookieConfig = ArgumentCaptor.forClass(CookieConfig.class);
        ArgumentCaptor<RedirectClaim> redirectCapture = ArgumentCaptor.forClass(RedirectClaim.class);

        CookieJwtException actual = new CookieJwtException("", new NullPointerException());
        doThrow(actual).when(mockCookieSigner).make(
                cookieConfig.capture(),
                redirectCapture.capture());

        Response<WebSiteSession> response = new Response<>();
        response.setCookies(new HashMap<>());

        String path = "/authorization?foo";
        subject.addRedirectForAuth(path, response);

        // should not have added the cookie.
        assertThat(response.getCookies(), is(notNullValue()));
        assertThat(response.getCookies().size(), is(0));

        // the claims to make cookie should have correct values
        assertThat(redirectCapture.getValue().getRedirect(), is(path));
        assertThat(redirectCapture.getValue().getDone(), is(false));

        // should use correct config to make the redirect cookie
        assertThat(cookieConfig.getValue(), is(redirectCookieConfig));
    }

    @Test
    public void readRedirectForAuthWhenDoneShouldAddMessage() throws Exception {
        Cookie redirectCookie = new Cookie.Builder()
                .name(CookieName.REDIRECT.toString())
                .value("some-jwt")
                .build();

        Response<WebSiteSession> response = new Response<>();
        response.setCookies(new HashMap<>());
        response.getCookies().put(redirectCookie.getName(), redirectCookie);

        AuthorizationPresenter presenter = new AuthorizationPresenter();

        RedirectClaim redirectClaim = new RedirectClaim();
        redirectClaim.setRedirect("/authorization?foo");
        redirectClaim.setDone(true);

        ReadEither<RedirectClaim> readEither = new ReadEither.Builder<RedirectClaim>()
                .right(redirectClaim)
                .build();

        when(mockCookieSigner.read(eq(redirectCookie.getValue()), eq(RedirectClaim.class)))
                .thenReturn(readEither);

        subject.readRedirectForAuth(redirectCookie, presenter, response);

        assertThat(presenter.getUserMessage(), is(notNullValue()));
        assertTrue(presenter.getUserMessage().isPresent());
        assertThat(presenter.getUserMessage().get(), is("Thanks for registering. We have sent you an email to verify your email address. You can now login."));

        // should not remove redirect cookie.
        assertThat(response.getCookies().size(), is(1));
    }

    @Test
    public void readRedirectForAuthWhenNotDoneShouldNotAddMessage() throws Exception {
        Cookie redirectCookie = new Cookie.Builder()
                .name(CookieName.REDIRECT.toString())
                .value("some-jwt")
                .build();

        Response<WebSiteSession> response = new Response<>();
        response.setCookies(new HashMap<>());
        response.getCookies().put(redirectCookie.getName(), redirectCookie);

        AuthorizationPresenter presenter = new AuthorizationPresenter();
        presenter.setUserMessage(Optional.empty());

        RedirectClaim redirectClaim = new RedirectClaim();
        redirectClaim.setRedirect("/authorization?foo");
        redirectClaim.setDone(false);

        ReadEither<RedirectClaim> readEither = new ReadEither.Builder<RedirectClaim>()
                .right(redirectClaim)
                .build();

        when(mockCookieSigner.read(eq(redirectCookie.getValue()), eq(RedirectClaim.class)))
                .thenReturn(readEither);

        subject.readRedirectForAuth(redirectCookie, presenter, response);

        assertThat(presenter.getUserMessage(), is(notNullValue()));
        assertFalse(presenter.getUserMessage().isPresent());

        // should not remove redirect cookie.
        assertThat(response.getCookies().size(), is(1));
    }

    @Test
    public void readRedirectForAuthWhenErrorAndCauseShouldRemoveCookie() throws Exception {
        Cookie redirectCookie = new Cookie.Builder()
                .name(CookieName.REDIRECT.toString())
                .value("some-jwt")
                .build();

        Response<WebSiteSession> response = new Response<>();
        response.setCookies(new HashMap<>());
        response.getCookies().put(redirectCookie.getName(), redirectCookie);

        AuthorizationPresenter presenter = new AuthorizationPresenter();
        presenter.setUserMessage(Optional.empty());

        RedirectClaim redirectClaim = new RedirectClaim();
        redirectClaim.setRedirect("/authorization?foo");
        redirectClaim.setDone(false);

        NullPointerException cause = new NullPointerException();
        ReadError<RedirectClaim> error = new ReadError.Builder<RedirectClaim>()
                .cause(cause)
                .cookieError(CookieError.SIGNATURE_ERROR)
                .claims(Optional.of(redirectClaim))
                .build();

        ReadEither<RedirectClaim> readEither = new ReadEither.Builder<RedirectClaim>()
                .left(error)
                .build();

        when(mockCookieSigner.read(eq(redirectCookie.getValue()), eq(RedirectClaim.class)))
                .thenReturn(readEither);

        subject.readRedirectForAuth(redirectCookie, presenter, response);

        assertThat(presenter.getUserMessage(), is(notNullValue()));
        assertFalse(presenter.getUserMessage().isPresent());

        // should remove redirect cookie.
        assertThat(response.getCookies().size(), is(0));
    }

    @Test
    public void readRedirectForAuthWhenErrorAndNoCauseShouldRemoveCookie() throws Exception {
        Cookie redirectCookie = new Cookie.Builder()
                .name(CookieName.REDIRECT.toString())
                .value("some-jwt")
                .build();

        Response<WebSiteSession> response = new Response<>();
        response.setCookies(new HashMap<>());
        response.getCookies().put(redirectCookie.getName(), redirectCookie);

        AuthorizationPresenter presenter = new AuthorizationPresenter();
        presenter.setUserMessage(Optional.empty());

        RedirectClaim redirectClaim = new RedirectClaim();
        redirectClaim.setRedirect("/authorization?foo");
        redirectClaim.setDone(false);

        ReadError<RedirectClaim> error = new ReadError.Builder<RedirectClaim>()
                .cookieError(CookieError.SIGNATURE_ERROR)
                .claims(Optional.of(redirectClaim))
                .build();

        ReadEither<RedirectClaim> readEither = new ReadEither.Builder<RedirectClaim>()
                .left(error)
                .build();

        when(mockCookieSigner.read(eq(redirectCookie.getValue()), eq(RedirectClaim.class)))
                .thenReturn(readEither);

        subject.readRedirectForAuth(redirectCookie, presenter, response);

        assertThat(presenter.getUserMessage(), is(notNullValue()));
        assertFalse(presenter.getUserMessage().isPresent());

        // should remove redirect cookie.
        assertThat(response.getCookies().size(), is(0));
    }

    @Test
    public void readRedirectForRegisterShouldSetDoneAndReturnTrue() throws Exception {
        Cookie originalRedirectCookie = new Cookie.Builder()
                .name(CookieName.REDIRECT.toString())
                .value("some-jwt")
                .build();

        Response<WebSiteSession> response = new Response<>();
        response.setHeaders(new HashMap<>());
        response.setCookies(new HashMap<>());
        response.getCookies().put(originalRedirectCookie.getName(), originalRedirectCookie);

        RedirectClaim originalRedirect = new RedirectClaim();
        originalRedirect.setRedirect("/authorization?foo");
        originalRedirect.setDone(true);

        ReadEither<RedirectClaim> readEither = new ReadEither.Builder<RedirectClaim>()
                .right(originalRedirect)
                .build();

        when(mockCookieSigner.read(eq(originalRedirectCookie.getValue()), eq(RedirectClaim.class)))
                .thenReturn(readEither);

        // needs to make a new cookie...
        ArgumentCaptor<CookieConfig> cookieConfig = ArgumentCaptor.forClass(CookieConfig.class);
        ArgumentCaptor<RedirectClaim> redirectCapture = ArgumentCaptor.forClass(RedirectClaim.class);

        Cookie redirectCookieDone = new Cookie.Builder()
                .value("some-jwt-done")
                .name(CookieName.REDIRECT.toString())
                .build();

        when(mockCookieSigner.make(
                cookieConfig.capture(),
                redirectCapture.capture())
        ).thenReturn(redirectCookieDone);

        boolean actual = subject.readRedirectForRegister(response, originalRedirectCookie);

        assertTrue(actual);

        // redirect cookie should still be there and be the one that is done.
        assertThat(response.getCookies(), is(notNullValue()));
        assertThat(response.getCookies().get(CookieName.REDIRECT.toString()), is(redirectCookieDone));

        // the claims to make cookie should have correct values
        assertThat(redirectCapture.getValue().getRedirect(), is(originalRedirect.getRedirect()));
        assertThat(redirectCapture.getValue().getDone(), is(true));

        // should use correct config to make the redirect cookie
        assertThat(cookieConfig.getValue(), is(redirectCookieConfig));

        // should actually do a redirect.
        assertThat(response.getStatusCode(), is(StatusCode.MOVED_TEMPORARILY));
        assertThat(response.getHeaders().size(), is(1));
        assertThat(response.getHeaders().get(Header.LOCATION.toString()), is(originalRedirect.getRedirect()));
    }

    @Test
    public void readRedirectForRegisterWhenErrorShouldReturnFalse() throws Exception {
        Cookie originalRedirectCookie = new Cookie.Builder()
                .name(CookieName.REDIRECT.toString())
                .value("some-jwt")
                .build();

        Response<WebSiteSession> response = new Response<>();
        response.setStatusCode(StatusCode.OK);
        response.setHeaders(new HashMap<>());
        response.setCookies(new HashMap<>());
        response.getCookies().put(originalRedirectCookie.getName(), originalRedirectCookie);

        RedirectClaim originalRedirect = new RedirectClaim();
        originalRedirect.setRedirect("/authorization?foo");
        originalRedirect.setDone(true);

        ReadError<RedirectClaim> error = new ReadError.Builder<RedirectClaim>()
                .cookieError(CookieError.SIGNATURE_ERROR)
                .claims(Optional.of(originalRedirect))
                .build();

        ReadEither<RedirectClaim> readEither = new ReadEither.Builder<RedirectClaim>()
                .left(error)
                .build();

        when(mockCookieSigner.read(eq(originalRedirectCookie.getValue()), eq(RedirectClaim.class)))
                .thenReturn(readEither);

        boolean actual = subject.readRedirectForRegister(response, originalRedirectCookie);

        assertFalse(actual);

        // never call make..
        ArgumentCaptor<CookieConfig> cookieConfig = ArgumentCaptor.forClass(CookieConfig.class);
        ArgumentCaptor<RedirectClaim> redirectCapture = ArgumentCaptor.forClass(RedirectClaim.class);
        verify(mockCookieSigner, never()).make(cookieConfig.capture(), redirectCapture.capture());

        // redirect cookie should still be there and be the one that is og.
        assertThat(response.getCookies(), is(notNullValue()));
        assertThat(response.getCookies().get(CookieName.REDIRECT.toString()), is(originalRedirectCookie));

        // should not do a redirect.
        assertThat(response.getStatusCode(), is(StatusCode.OK));
        assertThat(response.getHeaders().size(), is(0));
    }

    public void readRedirectForRegisterWhenUnableToMakeCookieShouldReturnFalse() throws Exception {
        Cookie originalRedirectCookie = new Cookie.Builder()
                .name(CookieName.REDIRECT.toString())
                .value("some-jwt")
                .build();

        Response<WebSiteSession> response = new Response<>();
        response.setStatusCode(StatusCode.OK);
        response.setHeaders(new HashMap<>());
        response.setCookies(new HashMap<>());
        response.getCookies().put(originalRedirectCookie.getName(), originalRedirectCookie);

        RedirectClaim originalRedirect = new RedirectClaim();
        originalRedirect.setRedirect("/authorization?foo");
        originalRedirect.setDone(true);

        ReadEither<RedirectClaim> readEither = new ReadEither.Builder<RedirectClaim>()
                .right(originalRedirect)
                .build();

        when(mockCookieSigner.read(eq(originalRedirectCookie.getValue()), eq(RedirectClaim.class)))
                .thenReturn(readEither);

        // needs to make a new cookie...
        ArgumentCaptor<CookieConfig> cookieConfig = ArgumentCaptor.forClass(CookieConfig.class);
        ArgumentCaptor<RedirectClaim> redirectCapture = ArgumentCaptor.forClass(RedirectClaim.class);

        CookieJwtException ex = new CookieJwtException("", new NullPointerException());
        doThrow(ex).when(mockCookieSigner).make(
                cookieConfig.capture(),
                redirectCapture.capture());

        boolean actual = subject.readRedirectForRegister(response, originalRedirectCookie);

        assertFalse(actual);

        // redirect cookie should still be there and be the original one
        assertThat(response.getCookies(), is(notNullValue()));
        assertThat(response.getCookies().get(CookieName.REDIRECT.toString()), is(originalRedirectCookie));

        // the claims to make cookie should have correct values
        assertThat(redirectCapture.getValue().getRedirect(), is(originalRedirect.getRedirect()));
        assertThat(redirectCapture.getValue().getDone(), is(true));

        // should use correct config to make the redirect cookie
        assertThat(cookieConfig.getValue(), is(redirectCookieConfig));

        // should not do a redirect.
        assertThat(response.getStatusCode(), is(StatusCode.OK));
        assertThat(response.getHeaders().size(), is(0));
    }
}