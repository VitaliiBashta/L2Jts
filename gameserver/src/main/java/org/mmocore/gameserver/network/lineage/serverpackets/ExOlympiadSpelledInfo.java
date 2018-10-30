package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

import java.util.ArrayList;
import java.util.List;


public class ExOlympiadSpelledInfo extends GameServerPacket {
    private final List<Effect> effects;
    // chdd(dhd)
    private int char_obj_id = 0;

    public ExOlympiadSpelledInfo() {
        effects = new ArrayList<>();
    }

    public void addEffect(final int skillId, final int level, final int duration) {
        effects.add(new Effect(skillId, level, duration));
    }

    public void addSpellRecivedPlayer(final Player cha) {
        if (cha != null) {
            char_obj_id = cha.getObjectId();
        }
    }

    @Override
    protected final void writeData() {
        writeD(char_obj_id);
        writeD(effects.size());
        for (final Effect temp : effects) {
            writeD(temp.skillId);
            writeH(temp.level);
            writeD(temp.duration);
        }
    }

    static class Effect {
        final int skillId;
        final int level;
        final int duration;

        public Effect(final int skillId, final int level, final int duration) {
            this.skillId = skillId;
            this.level = level;
            this.duration = duration;
        }
    }
}