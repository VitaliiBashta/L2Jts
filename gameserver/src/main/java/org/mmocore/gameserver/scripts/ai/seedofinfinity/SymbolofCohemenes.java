package org.mmocore.gameserver.scripts.ai.seedofinfinity;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * @author pchayka
 */

public class SymbolofCohemenes extends DefaultAI {

    public SymbolofCohemenes(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
    }

    @Override
    protected void onEvtAggression(Creature target, int aggro) {
    }
}