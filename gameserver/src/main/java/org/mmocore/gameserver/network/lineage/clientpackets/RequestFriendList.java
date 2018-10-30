package org.mmocore.gameserver.network.lineage.clientpackets;


import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.friend.Friend;
import org.mmocore.gameserver.world.World;

import java.util.Map;

public class RequestFriendList extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        activeChar.sendPacket(SystemMsg._FRIENDS_LIST_);
        final Map<Integer, Friend> _list = activeChar.getFriendComponent().getList();
        for (final Map.Entry<Integer, Friend> entry : _list.entrySet()) {
            final Player friend = World.getPlayer(entry.getKey());
            if (friend != null) {
                activeChar.sendPacket(new SystemMessage(SystemMsg.S1_CURRENTLY_ONLINE).addName(friend));
            } else {
                activeChar.sendPacket(new SystemMessage(SystemMsg.S1_CURRENTLY_OFFLINE).addString(entry.getValue().getName()));
            }
        }
        activeChar.sendPacket(SystemMsg.__EQUALS__);
    }
}
