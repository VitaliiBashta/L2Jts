package org.mmocore.gameserver.scripts.ai;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * @author pchayka
 */
public class DeluLizardmanSpecialCommander extends Fighter {
    private boolean firstTimeAttacked = true;

    public DeluLizardmanSpecialCommander(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();
        if (actor == null)
            return;

        if (firstTimeAttacked) {
            firstTimeAttacked = false;
            if (Rnd.chance(40))
                Functions.npcSay(actor, NpcString.CONE_ON_ILL_TAKE_YOU_ON, attacker.getName());
        } else if (Rnd.chance(15))
            Functions.npcSay(actor, NpcString.HOW_DARE_YOU_INTERRUPT_A_SACRED_DUEL_YOU_MUST_BE_TAUGHT_A_LESSON);

        super.onEvtAttacked(attacker, skill, damage);
    }
}