package org.mmocore.gameserver.scripts.ai.seedofinfinity;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.scripts.instances.*;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * @author pchayka
 */

public class SoulCoffin extends DefaultAI {
    private long checkTimer = 0;

    public SoulCoffin(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }

    @Override
    protected void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        Reflection r = actor.getReflection();
        if (!r.isDefault()) {
            if (actor.getNpcId() == 18711) {
                if (r.getInstancedZoneId() == 119) {
                    ((ErosionHallAttack) r).notifyCoffinDeath();
                } else if (r.getInstancedZoneId() == 121) {
                    ((HeartInfinityAttack) r).notifyCoffinDeath();
                } else if (r.getInstancedZoneId() == 120) {
                    ((ErosionHallDefence) r).notifyCoffinDeath();
                } else if (r.getInstancedZoneId() == 122) {
                    ((HeartInfinityDefence) r).notifyCoffinDeath();
                }
            }
        }
        super.onEvtDead(killer);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.getNpcId() == 18706 && actor.getReflection().getInstancedZoneId() == 116 && checkTimer + 10000 < System.currentTimeMillis()) {
            checkTimer = System.currentTimeMillis();
            ((SufferingHallDefence) actor.getReflection()).notifyCoffinActivity();
        }
        return super.thinkActive();
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
    }

    @Override
    protected void onEvtAggression(Creature target, int aggro) {
    }
}