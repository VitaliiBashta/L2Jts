package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 */
public class ExPartyMemberRenamed extends GameServerPacket {
    private final int objectId;
    private int dominionId;
    private boolean isDisguised;

    public ExPartyMemberRenamed(final Player player) {
        objectId = player.getObjectId();
        final DominionSiegeEvent siegeEvent = player.getEvent(DominionSiegeEvent.class);
        if (siegeEvent != null) {
            isDisguised = siegeEvent.getObjects(DominionSiegeEvent.DISGUISE_PLAYERS).contains(player.getObjectId());
            dominionId = siegeEvent.getId();
        }
    }

    @Override
    protected void writeData() {
        writeD(objectId);
        writeD(isDisguised);
        writeD(isDisguised ? dominionId : 0);
    }
}