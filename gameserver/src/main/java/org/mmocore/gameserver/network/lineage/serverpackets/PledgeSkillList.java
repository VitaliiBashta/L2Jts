package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.pledge.SubUnit;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Reworked: VISTALL
 */
public class PledgeSkillList extends GameServerPacket {
    private final List<UnitSkillInfo> unitSkills = new ArrayList<>();
    private List<SkillInfo> allSkills = Collections.emptyList();

    public PledgeSkillList(final Clan clan) {
        final Collection<SkillEntry> skills = clan.getSkills();
        allSkills = new ArrayList<>(skills.size());

        for (final SkillEntry sk : skills) {
            allSkills.add(new SkillInfo(sk.getId(), sk.getLevel()));
        }

        for (final SubUnit subUnit : clan.getAllSubUnits()) {
            for (final SkillEntry sk : subUnit.getSkills()) {
                unitSkills.add(new UnitSkillInfo(subUnit.getType(), sk.getId(), sk.getLevel()));
            }
        }
    }

    @Override
    protected final void writeData() {
        writeD(allSkills.size());
        writeD(unitSkills.size());

        for (final SkillInfo info : allSkills) {
            writeD(info.id);
            writeD(info.level);
        }

        for (final UnitSkillInfo info : unitSkills) {
            writeD(info.type);
            writeD(info.id);
            writeD(info.level);
        }
    }

    static class SkillInfo {
        public final int id;
        public final int level;

        public SkillInfo(final int id, final int level) {
            this.id = id;
            this.level = level;
        }
    }

    static class UnitSkillInfo extends SkillInfo {
        private final int type;

        public UnitSkillInfo(final int type, final int id, final int level) {
            super(id, level);
            this.type = type;
        }
    }
}