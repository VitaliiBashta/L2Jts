package org.mmocore.gameserver.scripts.ai.pts.hellbound;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.ScriptEvent;
import org.mmocore.gameserver.manager.HellboundManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.scripts.ai.FollowNpc;
import org.mmocore.gameserver.utils.ChatUtils;

/**
 * @author KilRoy
 */
public class ai_quarry_slave extends FollowNpc {
    public ai_quarry_slave(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtScriptEvent(ScriptEvent event, Object arg1, Object arg2) {
        final NpcInstance actor = (NpcInstance) getActor();
        if (actor != null && !actor.isDead()) {
            if (event == ScriptEvent.SCE_QUARRY_SLAVE_SEE_SH) {
                final int i0 = HellboundManager.getHellboundLevel();
                if (i0 == 5) {
                    HellboundManager.addConfidence(50);
                }
                ChatUtils.say(actor, NpcString.THANK_YOU_FOR_THE_RESCUE_ITS_A_SMALL_GIFT);
                if (actor.getFollowTarget() != null && actor.getFollowTarget().isPlayer()) {
                    if (Rnd.get(10000) < 261) {
                        actor.dropItem(actor.getFollowTarget().getPlayer(), 9628, 1);
                    } else if (Rnd.get(10000) < 175) {
                        actor.dropItem(actor.getFollowTarget().getPlayer(), 9630, 1);
                    } else if (Rnd.get(10000) < 145) {
                        actor.dropItem(actor.getFollowTarget().getPlayer(), 9629, 1);
                    } else if (Rnd.get(10000) < 6667) {
                        actor.dropItem(actor.getFollowTarget().getPlayer(), 1876, 1);
                    } else if (Rnd.get(10000) < 1333) {
                        actor.dropItem(actor.getFollowTarget().getPlayer(), 1877, 1);
                    } else if (Rnd.get(10000) < 2222) {
                        actor.dropItem(actor.getFollowTarget().getPlayer(), 1874, 1);
                    }
                }
                actor.suicide(null);
                actor.endDecayTask();
            }
        }
    }

    public void broadcastSE(final int seId) {
        if (seId == 1000001) {
            broadCastScriptEvent(ScriptEvent.SCE_QUARRY_SLAVE_SEE, getActor(), 500);
        }
    }

    public void setFollowMode(final Creature target) {
        getActor().setFollowTarget(target);
        getActor().setRunning();
        setIntention(CtrlIntention.AI_INTENTION_FOLLOW, target, 40);
    }
}