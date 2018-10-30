package org.mmocore.gameserver.data.xml.holder;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.network.authcomm.channel.AbstractServerChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @since 28.04.14
 */
public class IpConfigHolder extends AbstractHolder {
    public static final IpConfigHolder _instance = new IpConfigHolder();
    private List<AbstractServerChannel> _channels = new ArrayList<>();

    public static IpConfigHolder getInstance() {
        return _instance;
    }

    @Override
    public int size() {
        return _channels.size();
    }

    @Override
    public void clear() {
        _channels.clear();
    }

    public List<AbstractServerChannel> getChannels() {
        return _channels;
    }

    public void addChannel(AbstractServerChannel abstractServerChannel) {
        _channels.add(abstractServerChannel);
    }
}
