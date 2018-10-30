package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Creature;

/**
 * Format (ch)dddcccd
 * d: cahacter oid
 * d: time left
 * d: fish hp
 * c:
 * c:
 * c: 00 if fish gets damage 02 if fish regens
 * d:
 */
public class ExFishingHpRegen extends GameServerPacket {
    private final int time;
    private final int fishHP;
    private final int HPmode;
    private final int Anim;
    private final int GoodUse;
    private final int Penalty;
    private final int hpBarColor;
    private final int char_obj_id;

    public ExFishingHpRegen(final Creature character, final int time, final int fishHP, final int HPmode, final int GoodUse, final int anim, final int penalty, final int hpBarColor) {
        char_obj_id = character.getObjectId();
        this.time = time;
        this.fishHP = fishHP;
        this.HPmode = HPmode;
        this.GoodUse = GoodUse;
        Anim = anim;
        Penalty = penalty;
        this.hpBarColor = hpBarColor;
    }

    @Override
    protected final void writeData() {
        writeD(char_obj_id);
        writeD(time);
        writeD(fishHP);
        writeC(HPmode); // 0 = HP stop, 1 = HP raise
        writeC(GoodUse); // 0 = none, 1 = success, 2 = failed
        writeC(Anim); // Anim: 0 = none, 1 = reeling, 2 = pumping
        writeD(Penalty); // Penalty
        writeC(hpBarColor); // 0 = normal hp bar, 1 = purple hp bar

    }
}