package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.serverpackets.FriendList;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 23:36/22.03.2011
 */
public class RequestExFriendListForPostBox extends L2GameClientPacket {
    @Override
    protected void readImpl() {

    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        player.sendPacket(new FriendList(player));
    }
}
