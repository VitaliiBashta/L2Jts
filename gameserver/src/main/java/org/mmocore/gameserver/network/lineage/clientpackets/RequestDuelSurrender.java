package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.entity.events.impl.DuelEvent;
import org.mmocore.gameserver.object.Player;

public class RequestDuelSurrender extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        final DuelEvent duelEvent = player.getEvent(DuelEvent.class);
        if (duelEvent == null) {
            return;
        }

        duelEvent.packetSurrender(player);
    }
}