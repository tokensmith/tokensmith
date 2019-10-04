package org.rootservices.authorization.http.server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.authorization.http.controller.resource.authorization.AuthorizationResource;
import org.rootservices.otter.server.container.ServletContainer;
import org.rootservices.otter.server.container.ServletContainerFactory;
import org.rootservices.otter.config.OtterAppFactory;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by tommackenzie on 12/9/16.
 */
public class GizmoServer {
    private static final Logger logger = LogManager.getLogger(GizmoServer.class);
    public static String DOCUMENT_ROOT = "/";
    public static int PORT = 8080;
    private static String REQUEST_LOG = "logs/jetty/jetty-yyyy_mm_dd.request.log";

    public static void main(String[] args) {

        ServletContainer server = makeServer();

        try {
            logger.info("server starting");
            server.start();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        try {
            server.join();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static ServletContainer makeServer() {
        OtterAppFactory otterAppFactory = new OtterAppFactory();
        ServletContainerFactory servletContainerFactory = otterAppFactory.servletContainerFactory();

        ServletContainer server = null;
        try {
            server = servletContainerFactory.makeServletContainer(DOCUMENT_ROOT, AuthorizationResource.class, PORT, REQUEST_LOG);
        } catch (URISyntaxException e) {
            logger.error(e.getMessage(), e);
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return server;
    }


    public static ServletContainer makeServerFromWar() {
        OtterAppFactory otterAppFactory = new OtterAppFactory();
        ServletContainerFactory servletContainerFactory = otterAppFactory.servletContainerFactory();

        URI war = null;
        try {
            war = new URI("");
        } catch (URISyntaxException e) {
            logger.error(e.getMessage(), e);
        }

        ServletContainer server = null;
        try {
            server = servletContainerFactory.makeServletContainerFromWar(DOCUMENT_ROOT, war, PORT, REQUEST_LOG);
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return server;
    }
}
