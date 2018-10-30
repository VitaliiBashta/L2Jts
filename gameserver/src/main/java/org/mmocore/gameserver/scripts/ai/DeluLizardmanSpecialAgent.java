package org.mmocore.gameserver.scripts.ai;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Ranger;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * AI для Delu Lizardman Special Agent ID: 21105
 *
 * @author Diamond
 */
public class DeluLizardmanSpecialAgent extends Ranger {
    private boolean firstTimeAttacked = true;

    public DeluLizardmanSpecialAgent(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();
        if (firstTimeAttacked) {
            firstTimeAttacked = false;
            if (Rnd.chance(25)) {
                Functions.npcSay(actor, NpcString.S1_HOW_DARE_YOU_INTERRUPT_OUR_FIGHT_HEY_GUYS_HELP, attacker.getName());
            }
        } else if (Rnd.chance(10)) {
            Functions.npcSay(actor, NpcString.S1_HEY_WERE_HAVING_A_DUEL_HERE, attacker.getName());
        }
        super.onEvtAttacked(attacker, skill, damage);
    }
}