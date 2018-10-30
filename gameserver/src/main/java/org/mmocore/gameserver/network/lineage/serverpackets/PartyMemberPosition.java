package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;

import java.util.HashMap;
import java.util.Map;

public class PartyMemberPosition extends GameServerPacket {
    private final Map<Integer, Location> positions = new HashMap<>();

    public PartyMemberPosition add(final Player actor) {
        positions.put(actor.getObjectId(), actor.getLoc());
        return this;
    }

    public int size() {
        return positions.size();
    }

    @Override
    protected final void writeData() {
        writeD(positions.size());
        for (final Map.Entry<Integer, Location> e : positions.entrySet()) {
            writeD(e.getKey());
            writeD(e.getValue().x);
            writeD(e.getValue().y);
            writeD(e.getValue().z);
        }
    }
}