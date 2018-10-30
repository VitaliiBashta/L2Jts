package org.mmocore.gameserver.network.lineage.serverpackets.Moving;

import org.mmocore.gameserver.configuration.config.GeodataConfig;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.Log;

public class CharMoveToLocation extends GameServerPacket {
    private final int objectId;
    private final Location current;
    private int client_z_shift;
    private Location destination;

    public CharMoveToLocation(final Creature cha) {
        objectId = cha.getObjectId();
        current = cha.getLoc();
        destination = cha.getDestination();
        if (!cha.isFlying()) {
            client_z_shift = GeodataConfig.CLIENT_Z_SHIFT;
        }
        if (cha.isInWater()) {
            client_z_shift += GeodataConfig.CLIENT_Z_SHIFT;
        }

        if (destination == null) {
            Log.debug("CharMoveToLocation: desc is null, but moving. L2Character: " + cha.getObjectId() + ':' + cha.getName() + "; Loc: " + current);
            destination = current;
        }
    }

    public CharMoveToLocation(final int objectId, final Location from, final Location to) {
        this.objectId = objectId;
        current = from;
        destination = to;
    }

    @Override
    protected final void writeData() {
        writeD(objectId);

        writeD(destination.x);
        writeD(destination.y);
        writeD(destination.z + client_z_shift);

        writeD(current.x);
        writeD(current.y);
        writeD(current.z + client_z_shift);
    }
}