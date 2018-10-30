package org.mmocore.gameserver.scripts.ai;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public class Scarecrow extends Fighter {
    public Scarecrow(NpcInstance actor) {
        super(actor);
        actor.block();
        actor.setIsInvul(true);
    }

    @Override
    protected void onIntentionAttack(Creature target) {
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
    }

    @Override
    protected void onEvtAggression(Creature attacker, int aggro) {
    }
}