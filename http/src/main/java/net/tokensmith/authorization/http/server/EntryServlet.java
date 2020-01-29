package net.tokensmith.authorization.http.server;



import net.tokensmith.otter.gateway.Configure;
import net.tokensmith.otter.servlet.OtterEntryServlet;

import javax.servlet.annotation.WebServlet;


@WebServlet(value = "/app/*", name = "EntryServlet", asyncSupported = true)
public class EntryServlet extends OtterEntryServlet {

    @Override
    public Configure makeConfigure() {
        return new TokenSmithConfig();
    }
}
