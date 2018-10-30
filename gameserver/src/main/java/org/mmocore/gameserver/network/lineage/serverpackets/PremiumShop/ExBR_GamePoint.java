package org.mmocore.gameserver.network.lineage.serverpackets.PremiumShop;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

public class ExBR_GamePoint extends GameServerPacket {
    private final int objectId;
    private final long points;

    public ExBR_GamePoint(final Player player, final int points) {
        objectId = player.getObjectId();
        this.points = points;
    }

    @Override
    protected void writeData() {
        writeD(objectId);
        writeQ(points);
        writeD(0x00);   //??
    }
}