package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.friend.Friend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author VISTALL
 * @date 23:37/22.03.2011
 */
public class FriendList extends GameServerPacket {
    private List<FriendInfo> friends = Collections.emptyList();

    public FriendList(final Player player) {
        final Map<Integer, Friend> friends = player.getFriendComponent().getList();
        this.friends = new ArrayList<>(friends.size());
        for (final Map.Entry<Integer, Friend> entry : friends.entrySet()) {
            final Friend friend = entry.getValue();
            final FriendInfo f = new FriendInfo();
            f.name = friend.getName();
            f.classId = friend.getClassId();
            f.objectId = entry.getKey();
            f.level = friend.getLevel();
            f.online = friend.isOnline();
            this.friends.add(f);
        }
    }

    @Override
    protected void writeData() {
        writeD(friends.size());
        for (final FriendInfo f : friends) {
            writeD(f.objectId);
            writeS(f.name);
            writeD(f.online);
            writeD(f.online ? f.objectId : 0);
            writeD(f.classId);
            writeD(f.level);
        }
    }

    private static class FriendInfo {
        private String name;
        private int objectId;
        private boolean online;
        private int level;
        private int classId;
    }
}
