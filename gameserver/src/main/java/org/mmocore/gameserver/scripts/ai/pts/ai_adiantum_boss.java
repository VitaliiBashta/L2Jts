package org.mmocore.gameserver.scripts.ai.pts;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.world.World;

import java.util.concurrent.ScheduledFuture;

/**
 * @author Mangol, Magister
 * @version Freya
 */
public class ai_adiantum_boss extends Fighter {
    private static int i_ai0 = 0;
    // skill use
    private static int s_adiantum_water_strike_defect = 5703;
    private static int s_pailaka_water_strike = 5704;
    private static int s_display_adiantum_round_fire = 5754;
    private static int s_display_pailaka_trap_on = 5755;
    // other spawn mobs
    private static int pa36_trap_control = 18617;
    private static ScheduledFuture<?> ai_adiantum_boss_task_2000 = null;

    public ai_adiantum_boss(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean thinkActive() {
        final NpcInstance actor = getActor();
        if (actor == null) {
            return true;
        }
        if (i_ai0 == 0) {
            for (Player player : World.getAroundPlayers(actor, 300, 300)) {
                Player target = player.getPlayer();
                if (target != null) {
                    ThreadPoolManager.getInstance().schedule(new TimerId(target, 2000), 1000);
                    i_ai0 = 1;
                }
            }
        }
        return super.thinkActive();
    }

    private void clearn() {
        if (ai_adiantum_boss_task_2000 != null) {
            ai_adiantum_boss_task_2000.cancel(false);
        }
        ai_adiantum_boss_task_2000 = null;
        i_ai0 = 0;
    }

    private class TimerId extends RunnableImpl {
        final int _taskId;
        private Player _player;

        public TimerId(final int taskId) {
            _taskId = taskId;
        }

        public TimerId(final Player player, final int taskId) {
            _player = player;
            _taskId = taskId;
        }

        @Override
        public void runImpl() {
            final NpcInstance _actor = getActor();
            if (_actor != null) {
                switch (_taskId) {
                    case 2000: {
                        if (_actor != null && _player.getPlayer() != null) {
                            int i0 = Rnd.get(100);
                            if (_player.getPlayer() == null || _actor.getDistance(_player.getPlayer()) > 500) {
                                if (ai_adiantum_boss_task_2000 != null) {
                                    ai_adiantum_boss_task_2000.cancel(false);
                                }
                                ai_adiantum_boss_task_2000 = null;
                                ai_adiantum_boss_task_2000 = ThreadPoolManager.getInstance().schedule(new TimerId(2000), 3 * 1000);
                            }
                            if (i0 < 60) {
                                int i10 = Rnd.get(6);
                                switch (i10) {
                                    case 0:
                                    case 1: {
                                        _actor.doCast(SkillTable.getInstance().getSkillEntry(s_display_adiantum_round_fire, 1), _actor, true);
                                        if (_actor.getDistance(_player.getPlayer()) <= 300) {
                                            NpcUtils.spawnSingle(pa36_trap_control, _actor.getLoc(), _actor.getReflection());
                                        }
                                        break;
                                    }
                                    case 2:
                                    case 3:
                                        _actor.doCast(SkillTable.getInstance().getSkillEntry(s_adiantum_water_strike_defect, 2), _player.getPlayer(), true);
                                        break;
                                    case 4:
                                        _actor.doCast(SkillTable.getInstance().getSkillEntry(s_pailaka_water_strike, 1), _player.getPlayer(), true);
                                        break;
                                    case 5:
                                        _actor.doCast(SkillTable.getInstance().getSkillEntry(s_display_pailaka_trap_on, 1), _actor, true);
                                        int i1 = 0;
                                        int i2 = 0;
                                        int i3 = 0;
                                        int i5 = 0;
                                        if (185171 < _player.getY() || 185467 > _player.getY()) {
                                            i1 = 1;
                                        }
                                        if (184875 <= _player.getY() || 185171 >= _player.getY()) {
                                            i1 = 2;
                                        }
                                        if (184579 <= _player.getY() || 184875 > _player.getY()) {
                                            i1 = 3;
                                        }
                                        if (_player.getX() < -53300) {
                                            i2 = -54031;
                                            if (i1 == 1) {
                                                i3 = 185319;
                                            } else if (i1 == 2) {
                                                i3 = 185319 - 296;
                                            } else if (i1 == 3) {
                                                i3 = 185319 - 296 - 296;
                                            }
                                            i5 = 1;
                                            for (int i4 = 0; i4 < 6; i4 = i4 + 1) {
                                                int i6 = i2 + i4 * 150 + i5 * 22;
                                                NpcUtils.spawnSingle(pa36_trap_control, new Location(i6, i3, _player.getLoc().getZ()), _actor.getReflection());
                                            }
                                        } else {
                                            i2 = -52578;
                                            if (i1 == 1) {
                                                i3 = 185319;
                                            } else if (i1 == 2) {
                                                i3 = 185319 - 296;
                                            } else if (i1 == 3) {
                                                i3 = 185319 - 296 - 296;
                                            }
                                            i5 = 1;
                                            for (int i4 = 0; i4 < 6; i4 = i4 + 1) {
                                                int i6 = i2 - i4 * 150 - i5 * 22;
                                                NpcUtils.spawnSingle(pa36_trap_control, new Location(i6, i3, _player.getLoc().getZ()), _actor.getReflection());
                                            }
                                            break;
                                        }
                                }
                                break;
                            }
                            if (ai_adiantum_boss_task_2000 != null) {
                                ai_adiantum_boss_task_2000.cancel(false);
                            }
                            ai_adiantum_boss_task_2000 = null;
                            ai_adiantum_boss_task_2000 = ThreadPoolManager.getInstance().schedule(new TimerId(2000), 15 * 1000);
                        } else {
                            clearn();
                        }
                    }
                }
            }
        }
    }
}
