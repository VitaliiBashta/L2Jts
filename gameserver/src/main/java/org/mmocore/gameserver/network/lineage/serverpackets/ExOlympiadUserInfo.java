package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

public class ExOlympiadUserInfo extends GameServerPacket {
    // cdSddddd
    private final int side;
    private final int class_id;
    private final int curHp;
    private final int maxHp;
    private final int curCp;
    private final int maxCp;
    private final String name;
    private int obj_id = 0;

    public ExOlympiadUserInfo(final Player player, final int side) {
        this.side = side;
        obj_id = player.getObjectId();
        class_id = player.getPlayerClassComponent().getClassId().getId();
        name = player.getName();
        curHp = (int) player.getCurrentHp();
        maxHp = (int) player.getMaxHp();
        curCp = (int) player.getCurrentCp();
        maxCp = player.getMaxCp();
    }

    @Override
    protected final void writeData() {
        writeC(side);
        writeD(obj_id);
        writeS(name);
        writeD(class_id);
        writeD(curHp);
        writeD(maxHp);
        writeD(curCp);
        writeD(maxCp);
    }
}