package org.mmocore.gameserver.network.lineage.serverpackets;

import gnu.trove.map.TIntObjectMap;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 22:01/22.03.2011
 */
public class ExReceiveShowPostFriend extends GameServerPacket {
    private final TIntObjectMap<String> list;

    public ExReceiveShowPostFriend(final Player player) {
        list = player.getPostFriends();
    }

    @Override
    public void writeData() {
        writeD(list.size());
        for (final String t : list.valueCollection())
            writeS(t);
    }
}
