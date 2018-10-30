package org.mmocore.commons.net.xmlrpc;

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.server.XmlRpcStreamServer;
import org.apache.xmlrpc.webserver.WebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

/**
 * @author KilRoy
 */
public class XmlRpcServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlRpcServer.class);
    private final String host;
    private final int port;
    private final PropertyHandlerMapping pMapping;
    private WebServer webServer;

    public XmlRpcServer(final String host, final int port, final PropertyHandlerMapping pMapping) {
        this.host = host;
        this.port = port;
        this.pMapping = pMapping;
    }

    public void startServer(final String clientHost) {
        try {
            webServer = new WebServer(port, InetAddress.getByName(host));
            if (clientHost != null && !clientHost.equals("127.0.0.1"))
                webServer.acceptClient(clientHost);
            XmlRpcStreamServer xmlRpcServer = webServer.getXmlRpcServer();
            xmlRpcServer.setHandlerMapping(pMapping);
            XmlRpcServerConfigImpl rpcConfig = (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
            rpcConfig.setEnabledForExtensions(true);
            rpcConfig.setContentLengthOptional(false);
            final String client = clientHost.equals(host) ? "" : " for client " + clientHost;
            LOGGER.info("Listening" + client + " on " + host + ':' + port);
            webServer.start();
        } catch (Exception e) {
            LOGGER.error("return error in startServer(String) method: ", e.getLocalizedMessage());
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public PropertyHandlerMapping getpMapping() {
        return pMapping;
    }

    public WebServer getWebServer() {
        return webServer;
    }
}