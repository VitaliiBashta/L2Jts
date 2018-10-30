package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.entity.residence.Dominion;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * @author VISTALL
 */
public class ExReplyRegisterDominion extends GameServerPacket {
    private final int dominionId;
    private final int clanCount;
    private final int playerCount;
    private final boolean success;
    private final boolean join;
    private final boolean asClan;

    public ExReplyRegisterDominion(final Dominion dominion, final boolean success, final boolean join, final boolean asClan) {
        this.success = success;
        this.join = join;
        this.asClan = asClan;
        dominionId = dominion.getId();

        final DominionSiegeEvent siegeEvent = dominion.getSiegeEvent();

        playerCount = siegeEvent.getObjects(DominionSiegeEvent.DEFENDER_PLAYERS).size();
        clanCount = siegeEvent.getObjects(DominionSiegeEvent.DEFENDERS).size() + 1;
    }

    @Override
    protected void writeData() {
        writeD(dominionId);
        writeD(asClan);
        writeD(join);
        writeD(success);
        writeD(clanCount);
        writeD(playerCount);
    }
}