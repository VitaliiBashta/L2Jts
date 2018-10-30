package org.mmocore.gameserver.scripts.ai.pagan_temple;

import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.Mystic;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * AI охраны входа в Pagan Temple.<br>
 * <li>кидаются на всех игроков, у которых в кармане нету предмета 8064 или 8067
 * <li>не умеют ходить
 *
 * @author SYS
 */
public class GatekeeperZombie extends Mystic {
    public GatekeeperZombie(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }

    @Override
    public boolean checkAggression(Creature target) {
        NpcInstance actor = getActor();
        if (actor.isDead()) {
            return false;
        }
        if (getIntention() != CtrlIntention.AI_INTENTION_ACTIVE || !isGlobalAggro()) {
            return false;
        }
        if (target.isAlikeDead() || !target.isPlayable()) {
            return false;
        }
        if (!target.isInRangeZ(actor.getSpawnedLoc(), actor.getAggroRange())) {
            return false;
        }
        if (ItemFunctions.getItemCount((Playable) target, 8067) != 0 || ItemFunctions.getItemCount((Playable) target, 8064) != 0 || ItemFunctions.getItemCount((Playable) target, 8065) != 0) {
            return false;
        }
        if (!GeoEngine.canSeeTarget(actor, target, false)) {
            return false;
        }

        if (getIntention() != CtrlIntention.AI_INTENTION_ATTACK) {
            actor.getAggroList().addDamageHate(target, 0, 1);
            setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
        }

        return true;
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}