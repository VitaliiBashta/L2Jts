package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.entity.events.impl.KrateisCubeEvent;
import org.mmocore.gameserver.model.entity.events.objects.KrateisCubePlayerObject;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * @author VISTALL
 */
public class ExPVPMatchCCRecord extends GameServerPacket {
    private final KrateisCubePlayerObject[] players;

    public ExPVPMatchCCRecord(final KrateisCubeEvent cube) {
        players = cube.getSortedPlayers();
    }

    @Override
    public void writeData() {
        writeD(0x00); // Open/Dont Open
        writeD(players.length);
        for (final KrateisCubePlayerObject p : players) {
            writeS(p.getName());
            writeD(p.getPoints());
        }
    }
}