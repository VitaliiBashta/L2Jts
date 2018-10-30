package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.serverpackets.ExReceiveShowPostFriend;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 22:04/22.03.2011
 */
public class RequestExShowPostFriendListForPostBox extends L2GameClientPacket {
    @Override
    protected void readImpl() throws Exception {

    }

    @Override
    protected void runImpl() throws Exception {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        player.sendPacket(new ExReceiveShowPostFriend(player));
    }
}
