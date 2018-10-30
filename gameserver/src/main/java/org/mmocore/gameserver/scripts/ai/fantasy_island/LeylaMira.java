package org.mmocore.gameserver.scripts.ai.fantasy_island;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.PlaySound;
import org.mmocore.gameserver.network.lineage.serverpackets.PlaySound.Type;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.utils.Location;

/**
 * @author PaInKiLlEr - AI для Leyla Mira (32431). - Показывает социалки, проигровает музыку. - AI проверен и работает.
 */
public class LeylaMira extends DefaultAI {
    private static int count = 0;

    public LeylaMira(final NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }

        addTaskMove(new Location(-56657, -56338, -2006), true);
        doTask();
        ThreadPoolManager.getInstance().schedule(new ScheduleStart(1, actor), 5000);
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
        private final int _taskId;
        private final NpcInstance _actor;

        public ScheduleStart(final int taskId, final NpcInstance actor) {
            _taskId = taskId;
            _actor = actor;
        }

        @Override
        public void runImpl() {
            switch (_taskId) {
                case 1:
                    _actor.broadcastPacket(new PlaySound(Type.MUSIC, "NS22_F", 1, 0, _actor.getLoc()));
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(2, _actor), 100);
                    break;
                case 2:
                    _actor.broadcastPacket(new SocialAction(_actor.getObjectId(), 3));
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(3, _actor), 9000);
                    break;
                case 3:
                    if (count < 10) {
                        count++;
                        _actor.broadcastPacket(new SocialAction(_actor.getObjectId(), 1));
                        ThreadPoolManager.getInstance().schedule(new ScheduleStart(3, _actor), 3000);
                    } else {
                        count = 0;
                        ThreadPoolManager.getInstance().schedule(new ScheduleStart(4, _actor), 100);
                    }
                    break;
                case 4:
                    _actor.broadcastPacket(new SocialAction(_actor.getObjectId(), 2));
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(5, _actor), 3000);
                    break;
                case 5:
                    _actor.broadcastPacket(new SocialAction(_actor.getObjectId(), 1));
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(6, _actor), 36000);
                    break;
                case 6:
                    _actor.broadcastPacket(new SocialAction(_actor.getObjectId(), 2));
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(7, _actor), 3000);
                    break;
                case 7:
                    if (count < 2) {
                        count++;
                        _actor.broadcastPacket(new SocialAction(_actor.getObjectId(), 1));
                        ThreadPoolManager.getInstance().schedule(new ScheduleStart(7, _actor), 3000);
                    } else {
                        count = 0;
                        ThreadPoolManager.getInstance().schedule(new ScheduleStart(8, _actor), 100);
                    }
                    break;
                case 8:
                    _actor.broadcastPacket(new SocialAction(_actor.getObjectId(), 2));
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(9, _actor), 3000);
                    break;
                case 9:
                    _actor.broadcastPacket(new SocialAction(_actor.getObjectId(), 1));
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(10, _actor), 21000);
                    break;
                case 10:
                    if (count < 3) {
                        count++;
                        _actor.broadcastPacket(new SocialAction(_actor.getObjectId(), 1));
                        ThreadPoolManager.getInstance().schedule(new ScheduleStart(10, _actor), 3000);
                    } else {
                        count = 0;
                        ThreadPoolManager.getInstance().schedule(new ScheduleStart(11, _actor), 2000);
                    }
                    break;
                case 11:
                    _actor.broadcastPacket(new SocialAction(_actor.getObjectId(), 2));
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(12, _actor), 3000);
                    break;
                case 12:
                    if (count < 2) {
                        count++;
                        _actor.broadcastPacket(new SocialAction(_actor.getObjectId(), 1));
                        ThreadPoolManager.getInstance().schedule(new ScheduleStart(12, _actor), 3000);
                    } else {
                        count = 0;
                        ThreadPoolManager.getInstance().schedule(new ScheduleStart(13, _actor), 21000);
                    }
                    break;
                case 13:
                    if (count < 18) {
                        count++;
                        _actor.broadcastPacket(new SocialAction(_actor.getObjectId(), 1));
                        ThreadPoolManager.getInstance().schedule(new ScheduleStart(13, _actor), 3000);
                    } else
                        count = 0;
                    break;
            }
        }
    }

    private class ScheduleMoveFinish extends RunnableImpl {
        @Override
        public void runImpl() {
            addTaskMove(new Location(-56594, -56064, -1988), true);
            doTask();
        }
    }
}