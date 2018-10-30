package org.mmocore.gameserver.scripts.ai.breka_strongold;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.ChatUtils;

/**
 * @author PaInKiLlEr - AI для монстра Breka Orc Overlord (20270). - При атаке кричат в чат. - AI проверен и работает.
 */
public class BrekaOrcOverlord extends Fighter {
    private boolean firstTimeAttacked = true;

    public BrekaOrcOverlord(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();
        if (actor == null)
            return;

        if (firstTimeAttacked) {
            firstTimeAttacked = false;
            ChatUtils.say(actor, NpcString.I_NEVER_THOUGHT_ID_USE_THIS_AGAINST_A_NOVICE);
        }

        super.onEvtAttacked(attacker, skill, damage);
    }
}