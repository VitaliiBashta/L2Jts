package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Servitor;

public class ExPartyPetWindowAdd extends GameServerPacket {
    private final int ownerId, npcId, type, curHp, maxHp, curMp, maxMp, level;
    private final int summonId;
    private final String name;

    public ExPartyPetWindowAdd(final Servitor summon) {
        summonId = summon.getObjectId();
        ownerId = summon.getPlayer().getObjectId();
        npcId = summon.getTemplate().npcId + 1000000;
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
        writeD(summonId);
        writeD(npcId);
        writeD(type);
        writeD(ownerId);
        writeS(name);
        writeD(curHp);
        writeD(maxHp);
        writeD(curMp);
        writeD(maxMp);
        writeD(level);
    }
}