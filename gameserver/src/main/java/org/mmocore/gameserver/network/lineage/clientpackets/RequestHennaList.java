package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.serverpackets.HennaEquipList;
import org.mmocore.gameserver.object.Player;

public class RequestHennaList extends L2GameClientPacket {
    @Override
    protected void readImpl() {
        //readD(); - unknown
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        player.sendPacket(new HennaEquipList(player));
    }
}