package org.rootservices.authorization.http.server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.authorization.http.controller.resource.api.HealthResource;
import org.rootservices.otter.server.HttpServer;
import org.rootservices.otter.server.HttpServerConfig;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tommackenzie on 12/9/16.
 */
public class GizmoServer extends HttpServer {
    private static final Logger logger = LogManager.getLogger(GizmoServer.class);
    public static String DOCUMENT_ROOT = "/";
    public static int PORT = 8080;
    private static String REQUEST_LOG = "logs/jetty/jetty-yyyy_mm_dd.request.log";

    public static void main(String[] args) {

        List<String> gzipMimeTypes = new ArrayList<>();

        HttpServerConfig config = new HttpServerConfig(
                DOCUMENT_ROOT, PORT, REQUEST_LOG, HealthResource.class, gzipMimeTypes, new ArrayList<>()
        );
        run(config);
    }
}
