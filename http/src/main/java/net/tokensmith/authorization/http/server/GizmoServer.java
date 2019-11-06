package net.tokensmith.authorization.http.server;

import net.tokensmith.otter.server.HttpServer;
import net.tokensmith.otter.server.HttpServerConfig;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import net.tokensmith.authorization.http.controller.resource.api.HealthResource;


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

        List<String> gzipMimeTypes = Arrays.asList(
                "text/html", "text/plain", "text/xml",
                "text/css", "application/javascript", "text/javascript",
                "application/json");

        HttpServerConfig config = new HttpServerConfig(
                DOCUMENT_ROOT, PORT, REQUEST_LOG, HealthResource.class, gzipMimeTypes, new ArrayList<>()
        );
        run(config);
    }
}
