package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTreeTable;

import java.util.ArrayList;
import java.util.List;


/**
 * format   d (dddc)
 */
public class SkillList extends GameServerPacket {
    private final List<SkillEntry> skills;
    private final boolean canEnchant;

    public SkillList(final Player p) {
        skills = new ArrayList<>(p.getAllSkills());
        canEnchant = !p.isTransformed();
    }

    @Override
    protected final void writeData() {
        writeD(skills.size());

        for (final SkillEntry temp : skills) {
            writeD(temp.getTemplate().isActive() || temp.getTemplate().isToggle() ? 0 : 1); // deprecated? клиентом игнорируется
            writeD(temp.getDisplayLevel());
            writeD(temp.getDisplayId());
            writeC(temp.isDisabled() ? 0x01 : 0x00); // иконка скилла серая если не 0
            writeC(canEnchant ? (SkillTreeTable.isEnchantable(temp) ? 1 : 0) : 0); // для заточки: если 1 скилл можно точить
        }
    }
}