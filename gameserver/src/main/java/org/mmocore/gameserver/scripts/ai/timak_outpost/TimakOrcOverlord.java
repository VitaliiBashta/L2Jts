package org.mmocore.gameserver.scripts.ai.timak_outpost;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * @author PaInKiLlEr - AI для монстра Timak Orc Overlord (20588). - Кричит при ударе. - AI проверен и работает.
 */
public class TimakOrcOverlord extends Fighter {
    private boolean firstTimeAttacked = true;

    public TimakOrcOverlord(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();
        if (actor == null)
            return;

        if (firstTimeAttacked) {
            firstTimeAttacked = false;
            Functions.npcSay(actor, NpcString.DEAR_ULTIMATE_POWER);
        }

        super.onEvtAttacked(attacker, skill, damage);
    }
}