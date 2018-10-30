package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.instances.DoorInstance;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * 60
 * d6 6d c0 4b		door id
 * 8f 14 00 00 		x
 * b7 f1 00 00 		y
 * 60 f2 ff ff 		z
 * 00 00 00 00 		??
 * <p/>
 * format  dddd    rev 377  ID:%d X:%d Y:%d Z:%d
 * ddddd   rev 419
 */
public class DoorInfo extends GameServerPacket {
    private final int obj_id;
    private final int door_id;
    private final int view_hp;

    @Deprecated
    public DoorInfo(final DoorInstance door) {
        obj_id = door.getObjectId();
        door_id = door.getDoorId();
        view_hp = door.isHPVisible() ? 1 : 0;
    }

    @Override
    protected final void writeData() {
        writeD(obj_id);
        writeD(door_id);
        writeD(view_hp); // отображать ли хп у двери или стены
    }
}