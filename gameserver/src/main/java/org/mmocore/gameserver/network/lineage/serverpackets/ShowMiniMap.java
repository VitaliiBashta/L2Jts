package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.entity.SevenSigns;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

public class ShowMiniMap extends GameServerPacket {
    private final int mapId;
    private final int period;

    public ShowMiniMap(final Player player, final int mapId) {
        this.mapId = mapId;
        period = SevenSigns.getInstance().getCurrentPeriod();
    }

    @Override
    protected final void writeData() {
        writeD(mapId);
        writeC(period);
    }
}