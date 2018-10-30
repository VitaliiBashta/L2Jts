package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.object.Player;

public class RequestFriendDel extends L2GameClientPacket {
    private String _name;

    @Override
    protected void readImpl() {
        _name = readS(16);
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        player.getFriendComponent().removeFriend(_name);
    }
}