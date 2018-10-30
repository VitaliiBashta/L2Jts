package org.mmocore.gameserver.scripts.ai.pts.dragon_valley;

import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.base.InvisibleType;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_event_dc_monster extends Fighter {
    private static final int spawnTimer = 20100504;

    public ai_event_dc_monster(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        final int delayTime = getActor().getParameter("delayTime", 0);
        if (delayTime != 0) {
            getActor().block();
            getActor().setInvisibleType(InvisibleType.NORMAL);
            AddTimerEx(spawnTimer, delayTime * 100);
        }
    }

    @Override
    protected void onEvtTimerFiredEx(int timer_id, Object arg1, Object arg2) {
        final NpcInstance actor = getActor();
        if (actor == null || actor.isDead()) {
            return;
        }
        if (timer_id == spawnTimer) {
            actor.unblock();
            getActor().setInvisibleType(InvisibleType.NONE);
        }
    }

    @Override
    public boolean checkAggression(Creature target) {
        final NpcInstance actor = getActor();
        if (target.isPlayable() && !target.isDead() && !target.isInvisible()) {
            actor.getAggroList().addDamageHate(target, 0, 1);
            setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
        }
        return true;
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    @Override
    protected boolean maybeMoveToHome() {
        return false;
    }

    @Override
    protected void teleportHome() {
    }
}