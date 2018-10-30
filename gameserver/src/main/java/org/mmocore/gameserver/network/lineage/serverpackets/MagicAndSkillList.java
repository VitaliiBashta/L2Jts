package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Creature;

/**
 * Format:   ddd
 * Пример пакета:
 * 40
 * c8 22 00 49
 * be 50 00 00
 * 86 25 0b 00
 *
 * @author SYS
 */
public class MagicAndSkillList extends GameServerPacket {
    private final int chaId;
    private final int unk1;
    private final int unk2;

    public MagicAndSkillList(final Creature cha, final int unk1, final int unk2) {
        chaId = cha.getObjectId();
        this.unk1 = unk1;
        this.unk2 = unk2;
    }

    @Override
    protected final void writeData() {
        writeD(chaId);
        writeD(unk1); // в снифе было 20670
        writeD(unk2); // в снифе было 730502
    }
}