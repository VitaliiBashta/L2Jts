package org.mmocore.gameserver.network.authcomm.channel;

import org.mmocore.commons.net.utils.NetInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @since 29.04.14
 */
public abstract class AbstractServerChannel {
    private final List<NetInfo> _list = new ArrayList<>();
    private final int _id;

    public AbstractServerChannel(final int id) {
        _id = id;
    }

    public void add(final NetInfo i) {
        _list.add(i);
    }

    public List<NetInfo> getInfos() {
        return _list;
    }

    public int getId() {
        return _id;
    }
}
