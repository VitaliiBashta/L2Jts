package org.mmocore.gameserver.ai;

import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.npc.AggroList.AggroInfo;

public class Guard extends Fighter {
    public Guard(final NpcInstance actor) {
        super(actor);
    }

    public boolean canAttackCharacter(final Creature target) {
        final NpcInstance actor = getActor();
        if (getIntention() == CtrlIntention.AI_INTENTION_ATTACK) {
            final AggroInfo ai = actor.getAggroList().get(target);
            return ai != null && ai.hate > 0;
        }
        return target.isMonster() || target.isPlayable();
    }

    public boolean checkAggression(final Creature target) {
        final NpcInstance actor = getActor();
        if (getIntention() != CtrlIntention.AI_INTENTION_ACTIVE || !isGlobalAggro()) {
            return false;
        }

        if (target.isPlayable()) {
            if (target.getKarma() == 0 || (actor.getParameter("evilGuard", false) && target.getPvpFlag() > 0)) {
                return false;
            }
        }
        if (target.isMonster()) {
            if (!((MonsterInstance) target).isAggressive()) {
                return false;
            }
        }

        return super.checkAggression(target);
    }

    public int getMaxAttackTimeout() {
        return 0;
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}