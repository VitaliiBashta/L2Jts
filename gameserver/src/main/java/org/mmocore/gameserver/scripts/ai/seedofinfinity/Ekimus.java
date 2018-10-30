package org.mmocore.gameserver.scripts.ai.seedofinfinity;

import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Mystic;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.scripts.instances.HeartInfinityAttack;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * @author pchayka
 */
public class Ekimus extends Mystic {
    private long delayTimer = 0;

    public Ekimus(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean randomAnimation() {
        return false;
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();
        for (NpcInstance npc : actor.getReflection().getAllByNpcId(29151, true)) {
            npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, damage);
        }
        super.onEvtAttacked(attacker, skill, damage);
    }

    @Override
    protected void thinkAttack() {
        if (delayTimer + 5000 < System.currentTimeMillis()) {
            delayTimer = System.currentTimeMillis();
            if (getActor().getReflection().getInstancedZoneId() == 121) {
                ((HeartInfinityAttack) getActor().getReflection()).notifyEkimusAttack();
            }
        }
        super.thinkAttack();
    }

    @Override
    protected boolean thinkActive() {
        if (delayTimer + 5000 < System.currentTimeMillis()) {
            delayTimer = System.currentTimeMillis();
            if (getActor().getReflection().getInstancedZoneId() == 121) {
                ((HeartInfinityAttack) getActor().getReflection()).notifyEkimusIdle();
            }
        }
        return super.thinkActive();
    }
}