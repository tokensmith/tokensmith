package net.tokensmith.authorization.http.controller.resource.html.authorization.claim;


import net.tokensmith.jwt.entity.jwt.Claims;

public class RedirectClaim extends Claims {
    private String redirect;

    public RedirectClaim() {
    }

    public RedirectClaim(String redirect) {
        this.redirect = redirect;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }
}
