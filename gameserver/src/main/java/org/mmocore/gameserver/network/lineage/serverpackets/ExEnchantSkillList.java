package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

import java.util.ArrayList;
import java.util.List;

public class ExEnchantSkillList extends GameServerPacket {
    private final List<Skill> skills;
    private final EnchantSkillType type;
    public ExEnchantSkillList(final EnchantSkillType type) {
        this.type = type;
        skills = new ArrayList<>();
    }

    public void addSkill(final int id, final int level) {
        skills.add(new Skill(id, level));
    }

    @Override
    protected final void writeData() {
        writeD(type.ordinal());
        writeD(skills.size());
        for (final Skill sk : skills) {
            writeD(sk.id);
            writeD(sk.level);
        }
    }

    public enum EnchantSkillType {
        NORMAL,
        SAFE,
        UNTRAIN,
        CHANGE_ROUTE,
    }

    static class Skill {
        public final int id;
        public final int level;

        Skill(final int id, final int nextLevel) {
            this.id = id;
            level = nextLevel;
        }
    }
}