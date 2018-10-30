package org.mmocore.gameserver.scripts.ai.fantasy_island;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.Location;


/**
 * @author PaInKiLlEr - AI для Танцоров (32424, 32425, 32426, 32427, 32428, 32432). - Показывает социалки, кричат в чат. - AI проверен и работает.
 */
public class LeylaDancer extends DefaultAI {
    private static int count = 0;

    public LeylaDancer(final NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }

        ThreadPoolManager.getInstance().schedule(new ScheduleStart(), 6000);
        ThreadPoolManager.getInstance().schedule(new ScheduleMoveFinish(), 220000);
        super.onEvtSpawn();
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    @Override
    protected boolean randomAnimation() {
        return false;
    }

    private class ScheduleStart extends RunnableImpl {
        @Override
        public void runImpl() {
            final NpcInstance actor = getActor();
            if (actor != null) {
                if (count < 50) {
                    count++;
                    actor.broadcastPacket(new SocialAction(actor.getObjectId(), Rnd.get(1, 2)));
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(), 3600);
                } else {
                    count = 0;
                }
            }
        }
    }

    private class ScheduleMoveFinish extends RunnableImpl {
        @Override
        public void runImpl() {
            final NpcInstance actor = getActor();
            if (actor != null) {
                ChatUtils.shout(actor, NpcString.WE_LOVE_YOU);
                addTaskMove(new Location(-56594, -56064, -1988), true);
                doTask();
            }
        }
    }
}