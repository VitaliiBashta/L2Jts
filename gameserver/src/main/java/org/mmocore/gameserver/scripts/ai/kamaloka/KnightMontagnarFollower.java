package org.mmocore.gameserver.scripts.ai.kamaloka;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public class KnightMontagnarFollower extends Fighter {
    public KnightMontagnarFollower(NpcInstance actor) {
        super(actor);
        actor.setIsInvul(true);
    }

    @Override
    protected void onEvtAggression(Creature attacker, int aggro) {
        if (aggro < 1000000) {
            return;
        }
        super.onEvtAggression(attacker, aggro);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
    }
}