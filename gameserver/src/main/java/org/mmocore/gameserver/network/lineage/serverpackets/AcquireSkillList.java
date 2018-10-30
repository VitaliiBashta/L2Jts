package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.model.base.AcquireType;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

import java.util.ArrayList;
import java.util.List;


/**
 * Reworked: VISTALL
 */
public class AcquireSkillList extends GameServerPacket {
    private final AcquireType type;
    private final List<Skill> skills;

    public AcquireSkillList(final AcquireType type, final int size) {
        skills = new ArrayList<>(size);
        this.type = type;
    }

    public void addSkill(final int id, final int nextLevel, final int maxLevel, final int Cost, final int requirements, final int subUnit) {
        skills.add(new Skill(id, nextLevel, maxLevel, Cost, requirements, subUnit));
    }

    public void addSkill(final int id, final int nextLevel, final int maxLevel, final int Cost, final int requirements) {
        skills.add(new Skill(id, nextLevel, maxLevel, Cost, requirements, 0));
    }

    @Override
    protected final void writeData() {
        writeD(type.ordinal());
        writeD(skills.size());

        for (final Skill temp : skills) {
            writeD(temp.id);
            writeD(temp.nextLevel);
            writeD(temp.maxLevel);
            writeD(temp.cost);
            writeD(temp.requirements);
            if (type == AcquireType.SUB_UNIT) {
                writeD(temp.subUnit);
            }
        }
    }

    static class Skill {
        public final int id;
        public final int nextLevel;
        public final int maxLevel;
        public final int cost;
        public final int requirements;
        public final int subUnit;

        Skill(final int id, final int nextLevel, final int maxLevel, final int cost, final int requirements, final int subUnit) {
            this.id = id;
            this.nextLevel = nextLevel;
            this.maxLevel = maxLevel;
            this.cost = cost;
            this.requirements = requirements;
            this.subUnit = subUnit;
        }
    }
}