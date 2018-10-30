package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.cache.ItemInfoCache;
import org.mmocore.gameserver.network.lineage.serverpackets.ActionFail;
import org.mmocore.gameserver.network.lineage.serverpackets.ExRpItemLink;
import org.mmocore.gameserver.object.components.items.ItemInfo;

public class RequestExRqItemLink extends L2GameClientPacket {
    private int _objectId;

    @Override
    protected void readImpl() {
        _objectId = readD();
    }

    @Override
    protected void runImpl() {
        final ItemInfo item;
        if ((item = ItemInfoCache.getInstance().get(_objectId)) == null) {
            sendPacket(ActionFail.STATIC);
        } else {
            sendPacket(new ExRpItemLink(item));
        }
    }
}