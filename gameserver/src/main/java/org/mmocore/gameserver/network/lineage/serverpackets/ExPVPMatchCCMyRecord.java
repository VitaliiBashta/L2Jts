package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.entity.events.objects.KrateisCubePlayerObject;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * @author VISTALL
 */
public class ExPVPMatchCCMyRecord extends GameServerPacket {
    private final int points;

    public ExPVPMatchCCMyRecord(final KrateisCubePlayerObject player) {
        points = player.getPoints();
    }

    @Override
    public void writeData() {
        writeD(points);
    }
}