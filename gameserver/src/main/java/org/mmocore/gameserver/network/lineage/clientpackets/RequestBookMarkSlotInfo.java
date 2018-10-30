package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.serverpackets.ExGetBookMarkInfo;
import org.mmocore.gameserver.object.Player;

public class RequestBookMarkSlotInfo extends L2GameClientPacket {
    @Override
    protected void readImpl() {
        //
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        player.sendPacket(new ExGetBookMarkInfo(player));
    }
}