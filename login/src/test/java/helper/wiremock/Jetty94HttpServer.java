package helper.wiremock;


import com.github.tomakehurst.wiremock.common.JettySettings;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.http.AdminRequestHandler;
import com.github.tomakehurst.wiremock.http.StubRequestHandler;
import com.github.tomakehurst.wiremock.jetty9.JettyHttpServer;
import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.io.NetworkTrafficListener;
import org.eclipse.jetty.server.*;

public class Jetty94HttpServer extends JettyHttpServer {

    public Jetty94HttpServer(Options options, AdminRequestHandler adminRequestHandler, StubRequestHandler stubRequestHandler) {
        super(options, adminRequestHandler, stubRequestHandler);
    }

    protected ServerConnector createHttpConnector(
            String bindAddress,
            int port,
            JettySettings jettySettings,
            NetworkTrafficListener listener) {

        HttpConfiguration httpConfig = createHttpConfig(jettySettings);

        ConnectionFactory[] connectionFactories = new ConnectionFactory[] {
                new HttpConnectionFactory(httpConfig),
                new HTTP2CServerConnectionFactory(httpConfig)
        };

        ServerConnector connector = createServerConnector(
                bindAddress,
                jettySettings,
                port,
                listener,
                connectionFactories
        );

        return connector;
    }
}
