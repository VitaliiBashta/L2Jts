package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.model.base.EnchantSkillLearn;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.tables.SkillTreeTable;

import java.util.ArrayList;
import java.util.List;


public class ExEnchantSkillInfo extends GameServerPacket {
    private final List<Integer> routes;

    private final int id;
    private final int level;
    private int canAdd;
    private int canDecrease;

    public ExEnchantSkillInfo(final int id, final int level) {
        routes = new ArrayList<>();
        this.id = id;
        this.level = level;

        // skill already enchanted?
        if (level > 100) {
            canDecrease = 1;
            // get detail for next level
            final EnchantSkillLearn esd = SkillTreeTable.getSkillEnchant(this.id, this.level + 1);

            // if it exists add it
            if (esd != null) {
                addEnchantSkillDetail(esd.getLevel());
                canAdd = 1;
            }

            for (final EnchantSkillLearn el : SkillTreeTable.getEnchantsForChange(this.id, this.level)) {
                addEnchantSkillDetail(el.getLevel());
            }
        } else
        // not already enchanted
        {
            for (final EnchantSkillLearn esd : SkillTreeTable.getFirstEnchantsForSkill(this.id)) {
                addEnchantSkillDetail(esd.getLevel());
                canAdd = 1;
            }
        }
    }

    public void addEnchantSkillDetail(final int level) {
        routes.add(level);
    }

    @Override
    protected void writeData() {
        writeD(id);
        writeD(level);
        writeD(canAdd); // can add enchant
        writeD(canDecrease); // can decrease enchant

        writeD(routes.size());
        for (final Integer route : routes) {
            writeD(route);
        }
    }
}