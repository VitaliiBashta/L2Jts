package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

import java.util.Collection;


public class GMViewSkillInfo extends GameServerPacket {
    private final String charName;
    private final Collection<SkillEntry> skills;

    public GMViewSkillInfo(final Player cha) {
        charName = cha.getName();
        skills = cha.getAllSkills();
    }

    @Override
    protected final void writeData() {
        writeS(charName);
        writeD(skills.size());
        for (final SkillEntry skill : skills) {
            writeD(skill.getTemplate().isPassive() ? 1 : 0);
            writeD(skill.getDisplayLevel());
            writeD(skill.getId());
            writeC(skill.isDisabled() ? 0x01 : 0x00);
            writeC(SkillTable.getInstance().getMaxLevel(skill.getId()) > 100 ? 1 : 0);
        }
    }
}