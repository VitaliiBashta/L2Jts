package org.mmocore.gameserver.scripts.ai.pts;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.Mystic;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.world.World;

import java.util.concurrent.ScheduledFuture;

/**
 * @author Mangol, Magister
 * @version Freya
 */
public class ai_keym_trapopen extends Mystic {
    private static ScheduledFuture<?> ai_keym_trapopen_task_2000 = null;
    private static ScheduledFuture<?> ai_keym_trapopen_task_6000 = null;
    private static int ai_keym_trapopen = 0;

    public ai_keym_trapopen(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean thinkActive() {
        final NpcInstance actor = getActor();
        if (actor == null) {
            return true;
        }
        if (ai_keym_trapopen == 0) {
            for (Player player : World.getAroundPlayers(actor, 300, 300)) {
                Player target = player.getPlayer();
                if (target != null) {
                    ai_keym_trapopen = 1;
                    ThreadPoolManager.getInstance().schedule(new TimerId(target, 2000), 1000);
                    ThreadPoolManager.getInstance().schedule(new TimerId(target, 6000), 1000);
                }
            }
        }
        return super.thinkActive();
    }

    private void clearn() {
        if (ai_keym_trapopen_task_2000 != null) {
            ai_keym_trapopen_task_2000.cancel(false);
        }
        ai_keym_trapopen_task_2000 = null;
        if (ai_keym_trapopen_task_6000 != null) {
            ai_keym_trapopen_task_6000.cancel(false);
        }
        ai_keym_trapopen_task_6000 = null;
        ai_keym_trapopen = 0;
    }

    private class TimerId extends RunnableImpl {
        private final Player _player;
        private final int _taskId;

        public TimerId(final Player player, final int taskId) {
            _player = player;
            _taskId = taskId;
        }

        @Override
        public void runImpl() {
            final NpcInstance _actor = getActor();
            switch (_taskId) {
                case 2000:
                    if (_actor != null && _player.getPlayer() != null) {
                        if (_actor.getDistance(_player.getPlayer()) <= 300) {
                            if (Rnd.get(2) < 1) {
                                Functions.npcSay(_actor, NpcString.OHH_OH_OH);
                                NpcUtils.spawnSingle(18617, _actor.getLoc(), _actor.getReflection());
                            }
                        }
                        if (ai_keym_trapopen_task_2000 != null) {
                            ai_keym_trapopen_task_2000.cancel(false);
                        }
                        ai_keym_trapopen_task_2000 = null;
                        ai_keym_trapopen_task_2000 = ThreadPoolManager.getInstance().schedule(new TimerId(_player.getPlayer(), 2000), 15 * 1000);
                    } else {
                        clearn();
                    }
                    break;
                case 6000:
                    if (_actor != null && _player.getPlayer() != null) {
                        if (_actor.getDistance(_player.getPlayer()) <= 300) {
                            _actor.doCast(SkillTable.getInstance().getSkillEntry(5772, 1), _player.getPlayer(), true);
                        }
                        if (ai_keym_trapopen_task_6000 != null) {
                            ai_keym_trapopen_task_6000.cancel(false);
                        }
                        ai_keym_trapopen_task_6000 = null;
                        ai_keym_trapopen_task_2000 = ThreadPoolManager.getInstance().schedule(new TimerId(_player.getPlayer(), 6000), 60000);
                    } else {
                        clearn();
                    }
                    break;
            }
        }
    }
}
