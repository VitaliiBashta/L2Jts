package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

public class PartySmallWindowUpdate extends GameServerPacket {
    private final int obj_id;
    private final int class_id;
    private final int level;
    private final int curCp;
    private final int maxCp;
    private final int curHp;
    private final int maxHp;
    private final int curMp;
    private final int maxMp;
    private final String obj_name;

    public PartySmallWindowUpdate(final Player member) {
        obj_id = member.getObjectId();
        obj_name = member.getName();
        curCp = (int) member.getCurrentCp();
        maxCp = member.getMaxCp();
        curHp = (int) member.getCurrentHp();
        maxHp = (int) member.getMaxHp();
        curMp = (int) member.getCurrentMp();
        maxMp = member.getMaxMp();
        level = member.getLevel();
        class_id = member.getPlayerClassComponent().getClassId().getId();
    }

    @Override
    protected final void writeData() {
        //dSdddddddd
        writeD(obj_id);
        writeS(obj_name);
        writeD(curCp);
        writeD(maxCp);
        writeD(curHp);
        writeD(maxHp);
        writeD(curMp);
        writeD(maxMp);
        writeD(level);
        writeD(class_id);
    }
}