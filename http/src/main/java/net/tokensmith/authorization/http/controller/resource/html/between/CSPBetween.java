package net.tokensmith.authorization.http.controller.resource.html.between;

import net.tokensmith.authorization.http.controller.security.WebSiteSession;
import net.tokensmith.authorization.http.controller.security.WebSiteUser;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.between.Between;
import net.tokensmith.otter.router.exception.HaltException;

/**
 * Only allows Java Script from this domain.
 * https://cheatsheetseries.owasp.org/cheatsheets/Cross_Site_Scripting_Prevention_Cheat_Sheet.html
 */
public class CSPBetween implements Between<WebSiteSession, WebSiteUser> {
    private static final String CSP_HEADER = "Content-Security-Policy";

    @Override
    public void process(Method method, Request<WebSiteSession, WebSiteUser> request, Response<WebSiteSession> response) throws HaltException {
        StringBuilder policy = new StringBuilder()
                .append("default-src 'self';")
                .append("font-src fonts.gstatic.com;")
                .append("style-src 'self' fonts.googleapis.com");

        response.getHeaders().put(CSP_HEADER, policy.toString());
    }
}
