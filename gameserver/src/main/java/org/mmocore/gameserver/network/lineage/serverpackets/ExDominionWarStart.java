package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 12:08/05.03.2011
 */
public class ExDominionWarStart extends GameServerPacket {
    private final int objectId;
    private int territoryId, disguisedTerritoryId;
    private boolean isDisguised;
    private boolean battlefieldChatActive;

    public ExDominionWarStart(final Player player) {
        objectId = player.getObjectId();

        final DominionSiegeEvent siegeEvent = player.getEvent(DominionSiegeEvent.class);
        if (siegeEvent != null) {
            battlefieldChatActive = siegeEvent.hasState(DominionSiegeEvent.BATTLEFIELD_CHAT_STATE);
            territoryId = siegeEvent.isInProgress() ? siegeEvent.getId() : 0;

            isDisguised = siegeEvent.getObjects(DominionSiegeEvent.DISGUISE_PLAYERS).contains(objectId);
            if (isDisguised) {
                disguisedTerritoryId = siegeEvent.getId();
            }
        }
    }

    @Override
    protected void writeData() {
        writeD(objectId);
        writeD(battlefieldChatActive);
        writeD(territoryId);
        writeD(isDisguised);
        writeD(disguisedTerritoryId);
    }
}
