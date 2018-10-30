package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Servitor;

public class ExPartyPetWindowUpdate extends GameServerPacket {
    private final int owner_obj_id;
    private final int npc_id;
    private final int type;
    private final int curHp;
    private final int maxHp;
    private final int curMp;
    private final int maxMp;
    private final int level;
    private final String name;
    private int obj_id = 0;

    public ExPartyPetWindowUpdate(final Servitor summon) {
        obj_id = summon.getObjectId();
        owner_obj_id = summon.getPlayer().getObjectId();
        npc_id = summon.getTemplate().npcId + 1000000;
        type = summon.getServitorType();
        name = summon.getName();
        curHp = (int) summon.getCurrentHp();
        maxHp = (int) summon.getMaxHp();
        curMp = (int) summon.getCurrentMp();
        maxMp = summon.getMaxMp();
        level = summon.getLevel();
    }

    @Override
    protected final void writeData() {
        writeD(obj_id);
        writeD(npc_id);
        writeD(type);
        writeD(owner_obj_id);
        writeS(name);
        writeD(curHp);
        writeD(maxHp);
        writeD(curMp);
        writeD(maxMp);
        writeD(level);
    }
}