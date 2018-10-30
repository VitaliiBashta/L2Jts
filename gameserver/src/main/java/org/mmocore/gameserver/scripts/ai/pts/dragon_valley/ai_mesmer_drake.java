package org.mmocore.gameserver.scripts.ai.pts.dragon_valley;

import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_mesmer_drake extends Fighter {
    public ai_mesmer_drake(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean isGlobalAI() {
        return true;
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