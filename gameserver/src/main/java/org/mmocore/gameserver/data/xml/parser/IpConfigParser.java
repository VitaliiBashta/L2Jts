package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.commons.net.utils.Net;
import org.mmocore.commons.net.utils.NetInfo;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.IpConfigHolder;
import org.mmocore.gameserver.network.authcomm.channel.BaseServerChannel;
import org.mmocore.gameserver.network.authcomm.channel.ProxyServerChannel;

import java.io.File;

/**
 * @author VISTALL
 * @since 28.04.14
 */
public class IpConfigParser extends AbstractFileParser<IpConfigHolder> {
    public static final IpConfigParser _instance = new IpConfigParser();

    private IpConfigParser() {
        super(IpConfigHolder.getInstance());
    }

    public static IpConfigParser getInstance() {
        return _instance;
    }

    @Override
    public File getXMLFile() {
        return new File("configuration/ipconfig/ipconfig.xml");
    }

    @Override
    public String getDTDFileName() {
        return "ipconfig.dtd";
    }

    @Override
    protected void readData(IpConfigHolder holder, Element rootElement) throws Exception {
        if (!ServerConfig.IPCONFIG_ENABLE) {
            return;
        }
        for (final Element element : rootElement.getChildren("server")) {
            final String ipAddress = element.getAttributeValue("ip_address");
            final BaseServerChannel baseServerChannel = new BaseServerChannel(ServerConfig.REQUEST_ID);
            for (final Element subnet : element.getChildren("subnet")) {
                final Net mask = Net.valueOf(subnet.getAttributeValue("mask"));
                String ip = subnet.getAttributeValue("ip_address");
                if (ip == null) {
                    ip = ipAddress;
                }
                baseServerChannel.add(new NetInfo(mask, ip, ServerConfig.PORTS_GAME));
            }
            holder.addChannel(baseServerChannel);
        }

        for (final Element channelElement : rootElement.getChildren("channel")) {
            final int id = Integer.parseInt(channelElement.getAttributeValue("gameserverId"));
            final Element confElement = channelElement.getChild("conf");
            if (confElement == null) {
                return;
            }
            final String ip = confElement.getAttributeValue("address");
            final int port = Integer.parseInt(confElement.getAttributeValue("port"));
            holder.addChannel(new ProxyServerChannel(id, ip, port));
        }
    }
}
