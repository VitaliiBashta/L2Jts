package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.utils.EffectsComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PartySpelled extends GameServerPacket {
    private final int type;
    private final int objId;
    private final List<Effect> effects;

    public PartySpelled(final Playable activeChar, final boolean full) {
        objId = activeChar.getObjectId();
        type = activeChar.isPet() ? 1 : activeChar.isSummon() ? 2 : 0;
        // 0 - L2Player // 1 - петы // 2 - саммоны
        effects = new ArrayList<>();
        if (full) {
            final org.mmocore.gameserver.model.Effect[] effects = activeChar.getEffectList().getAllFirstEffects();
            Arrays.parallelSort(effects, EffectsComparator.getInstance());
            for (final org.mmocore.gameserver.model.Effect effect : effects) {
                if (effect != null && effect.isInUse()) {
                    effect.addPartySpelledIcon(this);
                }
            }
        }
    }

    @Override
    protected final void writeData() {
        writeD(type);
        writeD(objId);
        writeD(effects.size());
        for (final Effect temp : effects) {
            writeD(temp.skillId);
            writeH(temp.level);
            writeD(temp.duration);
        }
    }

    public void addPartySpelledEffect(final int skillId, final int level, final int duration) {
        effects.add(new Effect(skillId, level, duration));
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