package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.templates.item.support.LureType;

/**
 * Format (ch)dddcc
 */
public class ExFishingStartCombat extends GameServerPacket {
    final int time;
    final int hp;
    final LureType lureType;
    final int deceptiveMode;
    final int mode;
    private final int char_obj_id;

    public ExFishingStartCombat(final Creature character, final int time, final int hp, final int mode, final LureType lureType, final int deceptiveMode) {
        this.char_obj_id = character.getObjectId();
        this.time = time;
        this.hp = hp;
        this.mode = mode;
        this.lureType = lureType;
        this.deceptiveMode = deceptiveMode;
    }

    @Override
    protected final void writeData() {
        writeD(char_obj_id);
        writeD(time);
        writeD(hp);
        writeC(mode); // mode: 0 = resting, 1 = fighting
        writeC(lureType.ordinal()); // 0 = newbie lure, 1 = normal lure, 2 = night lure
        writeC(deceptiveMode); // Fish Deceptive Mode: 0 = no, 1 = yes
    }
}