package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.manager.MatchingRoomManager;
import org.mmocore.gameserver.object.Player;

/**
 * Format: (ch)
 */
public class RequestExitPartyMatchingWaitingRoom extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        MatchingRoomManager.getInstance().removeFromWaitingList(player);
    }
}