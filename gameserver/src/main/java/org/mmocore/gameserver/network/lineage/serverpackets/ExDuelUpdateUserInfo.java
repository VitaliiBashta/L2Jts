package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

/**
 * chsddddddddd
 * FE type
 * 4F 00 ex_type
 * 4E 00 65 00 6C 00 75 00 44 00 69 00 6D 00 00 00 name
 * 04 A6 C0 4C objectID?????
 * 2C 00 00 00 class_id
 * 06 00 00 00 level
 * 00 01 00 00 cur_hp
 * 00 01 00 00 max_hp
 * 4B 00 00 00 cur_mp
 * 4B 00 00 00 max_mp
 * 80 00 00 00 cur_cp
 * 80 00 00 00 max_cp
 */
public class ExDuelUpdateUserInfo extends GameServerPacket {
    private final String name;
    private final int obj_id;
    private final int class_id;
    private final int level;
    private final int curHp;
    private final int maxHp;
    private final int curMp;
    private final int maxMp;
    private final int curCp;
    private final int maxCp;

    public ExDuelUpdateUserInfo(final Player attacker) {
        name = attacker.getName();
        obj_id = attacker.getObjectId();
        class_id = attacker.getPlayerClassComponent().getClassId().getId();
        level = attacker.getLevel();
        curHp = (int) attacker.getCurrentHp();
        maxHp = (int) attacker.getMaxHp();
        curMp = (int) attacker.getCurrentMp();
        maxMp = attacker.getMaxMp();
        curCp = (int) attacker.getCurrentCp();
        maxCp = attacker.getMaxCp();
    }

    @Override
    protected final void writeData() {
        writeS(name);
        writeD(obj_id);
        writeD(class_id);
        writeD(level);
        writeD(curHp);
        writeD(maxHp);
        writeD(curMp);
        writeD(maxMp);
        writeD(curCp);
        writeD(maxCp);
    }
}