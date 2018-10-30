package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.entity.events.impl.FortressSiegeEvent;
import org.mmocore.gameserver.model.entity.residence.Fortress;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * @author VISTALL
 */
public class ExShowFortressMapInfo extends GameServerPacket {
    private final int fortressId;
    private final boolean fortressStatus;
    private final boolean[] commanders;

    public ExShowFortressMapInfo(final Fortress fortress) {
        fortressId = fortress.getId();
        fortressStatus = fortress.getSiegeEvent().isInProgress();

        final FortressSiegeEvent siegeEvent = fortress.getSiegeEvent();
        commanders = siegeEvent.getBarrackStatus();
    }

    @Override
    protected final void writeData() {
        writeD(fortressId);
        writeD(fortressStatus);
        writeD(commanders.length);
        for (final boolean b : commanders) {
            writeD(b);
        }
    }
}