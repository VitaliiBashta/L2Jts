package org.mmocore.gameserver.scripts.ai.residences.clanhall;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

/**
 * @author VISTALL
 * @date 16:29/22.04.2011
 */
public class MatchBerserker extends MatchFighter {
    public static final SkillEntry ATTACK_SKILL = SkillTable.getInstance().getSkillEntry(4032, 6);

    public MatchBerserker(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        super.onEvtAttacked(attacker, skill, damage);

        if (Rnd.chance(10)) {
            addTaskCast(attacker, ATTACK_SKILL);
        }
    }
}
