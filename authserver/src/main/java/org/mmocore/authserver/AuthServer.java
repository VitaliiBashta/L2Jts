package org.mmocore.authserver;

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.mmocore.authserver.configuration.config.LoginConfig;
import org.mmocore.authserver.configuration.loader.ConfigLoader;
import org.mmocore.authserver.database.DatabaseFactory;
import org.mmocore.authserver.manager.GameServerManager;
import org.mmocore.authserver.network.gamecomm.GameServerCommunication;
import org.mmocore.authserver.network.lineage.L2LoginClient;
import org.mmocore.authserver.network.lineage.L2LoginPacketHandler;
import org.mmocore.authserver.network.lineage.SelectorHelper;
import org.mmocore.authserver.network.xmlrpc.handler.AccountHandler;
import org.mmocore.commons.database.installer.DatabaseInstaller;
import org.mmocore.commons.net.nio.impl.SelectorConfig;
import org.mmocore.commons.net.nio.impl.SelectorThread;
import org.mmocore.commons.net.xmlrpc.XmlRpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AuthServer {
    private static final Logger _log = LoggerFactory.getLogger(AuthServer.class);

    private static final Path DATABASE_FILES_DIR = Paths.get("sql/");

    private static AuthServer authServer;
    private static XmlRpcServer xmlRpcServer;

    public AuthServer() throws Exception {
        DatabaseInstaller.start(DatabaseFactory.getInstance().getConnectionPool(), DATABASE_FILES_DIR);

        GameServerManager.getInstance();

        L2LoginPacketHandler loginPacketHandler = new L2LoginPacketHandler();
        SelectorHelper sh = new SelectorHelper();
        SelectorConfig sc = new SelectorConfig();
        final SelectorThread<L2LoginClient> _selectorThread = new SelectorThread<>(sc, loginPacketHandler, sh, sh, sh);

        final GameServerCommunication _gameServerListener = GameServerCommunication.getInstance();
        _gameServerListener.openServerSocket(LoginConfig.GAME_SERVER_LOGIN_HOST.equals("*") ? null : InetAddress.getByName(LoginConfig.GAME_SERVER_LOGIN_HOST), LoginConfig.GAME_SERVER_LOGIN_PORT);
        _gameServerListener.start();
        _log.info("Listening for gameservers on " + LoginConfig.GAME_SERVER_LOGIN_HOST + ':' + LoginConfig.GAME_SERVER_LOGIN_PORT);

        _selectorThread.openServerSocket(LoginConfig.LOGIN_HOST.equals("*") ? null : InetAddress.getByName(LoginConfig.LOGIN_HOST), LoginConfig.LOGIN_PORT);
        _selectorThread.start();
        _log.info("Listening for clients on " + LoginConfig.LOGIN_HOST + ':' + LoginConfig.LOGIN_PORT);

        // Initialize Xml-Rpc server
        final PropertyHandlerMapping pMapping = new PropertyHandlerMapping();
        pMapping.addHandler("AccountHandler", AccountHandler.class);
        xmlRpcServer = new XmlRpcServer(LoginConfig.XML_RPC_SERVER_HOST, LoginConfig.XML_RPC_SERVER_PORT, pMapping);
        xmlRpcServer.startServer(LoginConfig.XML_RPC_CLIENT_HOST);
    }

    public static AuthServer getInstance() {
        return authServer;
    }

    public static XmlRpcServer getXmlRpcServer() {
        return xmlRpcServer;
    }

    public static void checkFreePorts() throws Exception {
        ServerSocket ss = null;

        try {
            if (LoginConfig.LOGIN_HOST.equalsIgnoreCase("*")) {
                ss = new ServerSocket(LoginConfig.LOGIN_PORT);
            } else {
                ss = new ServerSocket(LoginConfig.LOGIN_PORT, 50, InetAddress.getByName(LoginConfig.LOGIN_HOST));
            }
        } finally {
            if (ss != null) {
                try {
                    ss.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static void main(final String... args) throws Exception {
        new File("./logs/").mkdir();
        // Initialize config
        ConfigLoader.loading();
        // Check binding address
        checkFreePorts();
        // Initialize database
        DatabaseFactory.getInstance().doStart();

        authServer = new AuthServer();
    }
}