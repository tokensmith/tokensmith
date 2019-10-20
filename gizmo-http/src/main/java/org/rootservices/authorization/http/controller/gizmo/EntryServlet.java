package org.rootservices.authorization.http.controller.gizmo;


import org.rootservices.otter.gateway.Configure;
import org.rootservices.otter.servlet.OtterEntryServlet;

import javax.servlet.annotation.WebServlet;


@WebServlet(value = "/app/*", name = "EntryServlet", asyncSupported = true)
public class EntryServlet extends OtterEntryServlet {

    @Override
    public Configure makeConfigure() {
        return new GizmoConfig();
    }
}
