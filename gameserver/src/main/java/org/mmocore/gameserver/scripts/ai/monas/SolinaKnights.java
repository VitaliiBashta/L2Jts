package org.mmocore.gameserver.scripts.ai.monas;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.world.World;

import java.util.concurrent.ScheduledFuture;

/**
 * @author PaInKiLlEr - AI для моба Solina Knights (18909). - Агрится на Scarecrow (18912). - Бьёт, отдыхает. - AI проверен и работает.
 */
public class SolinaKnights extends Fighter {
    private long _wait_timeout = System.currentTimeMillis() + 60000;
    private ScheduledFuture<?> _isAttack;
    private NpcInstance mob;

    public SolinaKnights(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor == null || actor.isDead())
            return true;

        if (_wait_timeout < System.currentTimeMillis()) {
            if (mob != null) {
                actor.stopMove();
                actor.setRunning();
                setIntention(CtrlIntention.AI_INTENTION_ATTACK, mob);
                mob.getAggroList().addDamageHate(actor, 10, 10000);
                _isAttack = ThreadPoolManager.getInstance().schedule(new ScheduleTimerTask(), 30000);
            }

            if (getIntention() == CtrlIntention.AI_INTENTION_ACTIVE && mob == null) {
                for (NpcInstance npc : World.getAroundNpc(actor, 150, 150)) {
                    if (npc.getNpcId() == 18912)
                        mob = npc;
                }
            }
            _wait_timeout = (System.currentTimeMillis() + 90000);
        }

        return true;
    }

    @Override
    protected void onEvtDead(Creature killer) {
        if (_isAttack != null) {
            _isAttack.cancel(false);
            _isAttack = null;
        }
        super.onEvtDead(killer);
    }

    private class ScheduleTimerTask extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            NpcInstance actor = getActor();
            if (actor.isRunning())
                actor.setWalking();

            if (getIntention() != CtrlIntention.AI_INTENTION_ACTIVE)
                setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
    }
}