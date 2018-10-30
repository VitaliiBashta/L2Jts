package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.friend.Friend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class L2FriendList extends GameServerPacket {
    private List<FriendInfo> list = Collections.emptyList();

    public L2FriendList(final Player player) {
        final Map<Integer, Friend> list = player.getFriendComponent().getList();
        this.list = new ArrayList<>(list.size());
        for (final Map.Entry<Integer, Friend> entry : list.entrySet()) {
            final FriendInfo f = new FriendInfo();
            f.objectId = entry.getKey();
            f.name = entry.getValue().getName();
            f.online = entry.getValue().isOnline();
            this.list.add(f);
        }
    }

    @Override
    protected final void writeData() {
        writeD(list.size());
        for (final FriendInfo friendInfo : list) {
            writeD(0);
            writeS(friendInfo.name); //name
            writeD(friendInfo.online ? 1 : 0); //online or offline
            writeD(friendInfo.objectId); //object_id
        }
    }

    private static class FriendInfo {
        private int objectId;
        private String name;
        private boolean online;
    }
}