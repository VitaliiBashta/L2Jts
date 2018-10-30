package org.mmocore.commons.net.utils;

/**
 * @author VISTALL
 * @since 29.04.14
 */
public class NetInfo {
    private Net _net;
    private String _host;
    private int _port;

    public NetInfo(Net net, String host, int port) {
        _net = net;
        _host = host;
        _port = port;
    }

    public int port() {
        return _port;
    }

    public Net net() {
        return _net;
    }

    public String host() {
        return _host;
    }
}
