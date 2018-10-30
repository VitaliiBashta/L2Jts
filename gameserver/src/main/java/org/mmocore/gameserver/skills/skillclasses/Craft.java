package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.network.lineage.serverpackets.RecipeBookItemList;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


public class Craft extends Skill {
    private final boolean _dwarven;

    public Craft(final StatsSet set) {
        super(set);
        _dwarven = set.getBool("isDwarven");
    }

    @Override
    public boolean checkCondition(final SkillEntry skillEntry, final Creature activeChar, final Creature target, final boolean forceUse, final boolean dontMove, final boolean first) {
        final Player p = (Player) activeChar;
        if (p.isInStoreMode() || p.isProcessingRequest()) {
            return false;
        }

        return super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        activeChar.sendPacket(new RecipeBookItemList((Player) activeChar, _dwarven));
    }
}
