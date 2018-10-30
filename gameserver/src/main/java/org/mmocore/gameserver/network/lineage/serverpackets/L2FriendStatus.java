package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

public class L2FriendStatus extends GameServerPacket {
    private final String charName;
    private final boolean login;

    public L2FriendStatus(final Player player, final boolean login) {
        this.login = login;
        charName = player.getName();
    }

    @Override
    protected final void writeData() {
        writeD(login ? 1 : 0); //Logged in 1 logged off 0
        writeS(charName);
        writeD(0); //id персонажа с базы оффа, не object_id
    }
}