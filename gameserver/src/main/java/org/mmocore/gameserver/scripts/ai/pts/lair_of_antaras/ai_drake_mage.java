package org.mmocore.gameserver.scripts.ai.pts.lair_of_antaras;

import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.Mystic;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_drake_mage extends Mystic {
    public ai_drake_mage(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean checkAggression(Creature target) {
        final NpcInstance actor = getActor();
        if (target.isPlayable() && !target.isDead() && !target.isInvisible()) {
            actor.getAggroList().addDamageHate(target, 0, 1);
            setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
            if (actor.getLeader() != null) {
                actor.getLeader().getAggroList().addDamageHate(target, 0, 1);
                actor.getLeader().getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
            }
            return true;
        }
        return false;
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

    @Override
    protected void onEvtSpawn() {
        if (getActor().getLeader() != null) {
            getActor().setFollowTarget(getActor().getLeader());
            getActor().setRunning();
            setIntention(CtrlIntention.AI_INTENTION_FOLLOW, getActor().getLeader(), AllSettingsConfig.FOLLOW_RANGE);
        }
        super.onEvtSpawn();
    }
}