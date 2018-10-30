package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.templates.item.support.FishGroup;
import org.mmocore.gameserver.templates.item.support.LureType;
import org.mmocore.gameserver.utils.Location;

/**
 * Format (ch)ddddd
 */
public class ExFishingStart extends GameServerPacket {
    private final int _charObjId;
    private final Location _loc;
    private final FishGroup _fishGroup;
    private final LureType _lureType;

    public ExFishingStart(final Creature character, final FishGroup fishGroup, final Location loc, final LureType type) {
        this._charObjId = character.getObjectId();
        this._fishGroup = fishGroup;
        this._loc = loc;
        this._lureType = type;
    }

    @Override
    protected final void writeData() {
        writeD(_charObjId);
        writeD(_fishGroup.ordinal()); // fish type
        writeD(_loc.x); // x poisson
        writeD(_loc.y); // y poisson
        writeD(_loc.z); // z poisson
        writeC(_lureType.ordinal()); // 0 = day lure  1 = night lure
        writeC(0x01); // result Button
    }
}