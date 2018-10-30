package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.World;

public class RequestReload extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        player.sendUserInfo(true);
        World.showObjectsToPlayer(player, false);
    }
}