package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.utils.Location;

public class PetStatusUpdate extends GameServerPacket {
    private final int type;
    private final int obj_id;
    private final int level;
    private final int maxFed;
    private final int curFed;
    private final int maxHp;
    private final int curHp;
    private final int maxMp;
    private final int curMp;
    private final long exp;
    private final long exp_this_lvl;
    private final long exp_next_lvl;
    private final Location loc;
    private final String title;

    public PetStatusUpdate(final Servitor summon) {
        type = summon.getServitorType();
        obj_id = summon.getObjectId();
        loc = summon.getLoc();
        title = summon.getTitle();
        curHp = (int) summon.getCurrentHp();
        maxHp = (int) summon.getMaxHp();
        curMp = (int) summon.getCurrentMp();
        maxMp = summon.getMaxMp();
        curFed = summon.getCurrentFed();
        maxFed = summon.getMaxFed();
        level = summon.getLevel();
        exp = summon.getExp();
        exp_this_lvl = summon.getExpForThisLevel();
        exp_next_lvl = summon.getExpForNextLevel();
    }

    @Override
    protected final void writeData() {
        writeD(type);
        writeD(obj_id);
        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z);
        writeS(title);
        writeD(curFed);
        writeD(maxFed);
        writeD(curHp);
        writeD(maxHp);
        writeD(curMp);
        writeD(maxMp);
        writeD(level);
        writeQ(exp);
        writeQ(exp_this_lvl);// 0% absolute value
        writeQ(exp_next_lvl);// 100% absolute value
    }
}