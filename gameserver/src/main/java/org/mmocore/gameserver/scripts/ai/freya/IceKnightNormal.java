package org.mmocore.gameserver.scripts.ai.freya;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.concurrent.ScheduledFuture;

/**
 * @author pchayka
 */

public class IceKnightNormal extends Fighter {
    private boolean iced;
    private ScheduledFuture<?> task;

    public IceKnightNormal(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        NpcInstance actor = getActor();
        iced = true;
        actor.setNpcState(1);
        actor.block();
        Reflection r = actor.getReflection();
        if (r != null && r.getPlayers() != null) {
            for (Player p : r.getPlayers()) {
                this.notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 300);
            }
        }

        task = ThreadPoolManager.getInstance().schedule(new ReleaseFromIce(), 6000L);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();

        if (iced) {
            iced = false;
            if (task != null) {
                task.cancel(false);
            }
            actor.unblock();
            actor.setNpcState(2);
        }
        super.onEvtAttacked(attacker, skill, damage);
    }

    @Override
    protected void teleportHome() {
    }

    private class ReleaseFromIce extends RunnableImpl {
        @Override
        public void runImpl() {
            if (iced) {
                iced = false;
                getActor().setNpcState(2);
                getActor().unblock();
            }
        }
    }
}