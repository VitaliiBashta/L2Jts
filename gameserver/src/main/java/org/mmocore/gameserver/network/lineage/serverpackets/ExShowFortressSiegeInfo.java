package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.entity.events.impl.FortressSiegeEvent;
import org.mmocore.gameserver.model.entity.residence.Fortress;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * @author VISTALL
 */
public class ExShowFortressSiegeInfo extends GameServerPacket {
    private final int fortressId;
    private final int commandersMax;
    private int commandersCurrent;

    public ExShowFortressSiegeInfo(final Fortress fortress) {
        fortressId = fortress.getId();

        final FortressSiegeEvent siegeEvent = fortress.getSiegeEvent();
        commandersMax = siegeEvent.getBarrackStatus().length;
        if (fortress.getSiegeEvent().isInProgress()) {
            for (int i = 0; i < commandersMax; i++) {
                if (siegeEvent.getBarrackStatus()[i]) {
                    commandersCurrent++;
                }
            }
        }
    }

    @Override
    protected void writeData() {
        writeD(fortressId);
        writeD(commandersMax);
        writeD(commandersCurrent);
    }
}