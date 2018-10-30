package org.mmocore.gameserver.network.lineage.serverpackets.Moving;

import org.mmocore.gameserver.configuration.config.GeodataConfig;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.utils.Location;

/**
 * format  dddd
 * <p/>
 * sample
 * 0000: 3a  69 08 10 48  02 c1 00 00  f7 56 00 00  89 ea ff    :i..H.....V.....
 * 0010: ff  0c b2 d8 61                                     ....a
 */
public class TeleportToLocation extends GameServerPacket {
    private final int targetId;
    private final Location loc;

    public TeleportToLocation(final GameObject cha, final Location loc) {
        targetId = cha.getObjectId();
        this.loc = loc;
    }

    public TeleportToLocation(final GameObject cha, final int x, final int y, final int z) {
        targetId = cha.getObjectId();
        loc = new Location(x, y, z, cha.getHeading());
    }

    @Override
    protected final void writeData() {
        writeD(targetId);
        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z + GeodataConfig.CLIENT_Z_SHIFT);
        writeD(0x00); //IsValidation
        writeD(loc.h);
    }
}