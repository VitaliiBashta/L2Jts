package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.utils.Location;

/**
 * format   dddddd
 */
public class Earthquake extends GameServerPacket {
    private final Location loc;
    private final int intensity;
    private final int duration;

    public Earthquake(final Location loc, final int intensity, final int duration) {
        this.loc = loc;
        this.intensity = intensity;
        this.duration = duration;
    }

    @Override
    protected final void writeData() {
        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z);
        writeD(intensity);
        writeD(duration);
        writeD(0x00); // Unknown
    }
}