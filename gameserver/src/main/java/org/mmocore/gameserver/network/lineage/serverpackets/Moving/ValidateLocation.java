package org.mmocore.gameserver.network.lineage.serverpackets.Moving;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.Location;

/**
 * format   dddddd		(player id, target id, distance, startx, starty, startz)<p>
 */
public class ValidateLocation extends GameServerPacket {
    private final int chaObjId;
    private final Location loc;

    public ValidateLocation(final Creature cha) {
        chaObjId = cha.getObjectId();
        loc = cha.getLoc();
    }

    @Override
    protected final void writeData() {
        writeD(chaObjId);
        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z);
        writeD(loc.h);
    }
}