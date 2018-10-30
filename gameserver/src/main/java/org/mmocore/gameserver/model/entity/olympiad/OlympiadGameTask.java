package org.mmocore.gameserver.model.entity.olympiad;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.OlympiadConfig;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;

public class OlympiadGameTask extends RunnableImpl {
    private static final Logger _log = LoggerFactory.getLogger(OlympiadGameTask.class);

    private final OlympiadGame _game;
    private final BattleStatus _status;
    private final int _count;
    private final long _time;

    private boolean _terminated = false;

    public OlympiadGameTask(final OlympiadGame game, final BattleStatus status, final int count, final long time) {
        _game = game;
        _status = status;
        _count = count;
        _time = time;
    }

    public boolean isTerminated() {
        return _terminated;
    }

    public BattleStatus getStatus() {
        return _status;
    }

    public int getCount() {
        return _count;
    }

    public OlympiadGame getGame() {
        return _game;
    }

    public long getTime() {
        return _count;
    }

    public ScheduledFuture<?> shedule() {
        return ThreadPoolManager.getInstance().schedule(this, _time);
    }

    @Override
    public void runImpl() throws Exception {
        if (_game == null || _terminated) {
            return;
        }

        OlympiadGameTask task = null;

        final int gameId = _game.getId();

        try {
            if (!Olympiad.inCompPeriod()) {
                return;
            }

            // Прерываем игру, если один из игроков не онлайн, и игра еще не прервана
            if (!_game.checkPlayersOnline() && _status != BattleStatus.ValidateWinner && _status != BattleStatus.Ending) {
                Log.add("Player is offline for game " + gameId + ", status: " + _status, "olympiad");
                _game.endGame(1, true);
                return;
            }

            switch (_status) {
                case Begining: {
                    task = new OlympiadGameTask(_game, BattleStatus.Begin_Countdown, OlympiadConfig.olympiadTeleTime, 100);
                    break;
                }
                case Begin_Countdown: {
                    _game.broadcastPacket(new SystemMessage(SystemMsg.YOU_WILL_BE_MOVED_TO_THE_OLYMPIAD_STADIUM_IN_S1_SECONDS).addNumber(_count), true, false);
                    switch (_count) {
                        case 120:
                            task = new OlympiadGameTask(_game, BattleStatus.Begin_Countdown, 60, 60000);
                            break;
                        case 60:
                            task = new OlympiadGameTask(_game, BattleStatus.Begin_Countdown, 30, 30000);
                            break;
                        case 30:
                            task = new OlympiadGameTask(_game, BattleStatus.Begin_Countdown, 15, 15000);
                            break;
                        case 15:
                            task = new OlympiadGameTask(_game, BattleStatus.Begin_Countdown, 5, 10000);
                            break;
                        case 5:
                            task = new OlympiadGameTask(_game, BattleStatus.Begin_Countdown, 4, 1000);
                            break;
                        case 4:
                            task = new OlympiadGameTask(_game, BattleStatus.Begin_Countdown, 3, 1000);
                            break;
                        case 3:
                            task = new OlympiadGameTask(_game, BattleStatus.Begin_Countdown, 2, 1000);
                            break;
                        case 2:
                            task = new OlympiadGameTask(_game, BattleStatus.Begin_Countdown, 1, 1000);
                            break;
                        case 1:
                            task = new OlympiadGameTask(_game, BattleStatus.PortPlayers, 0, 1000);
                            break;
                    }
                    break;
                }
                case PortPlayers: {
                    _game.portPlayersToArena();
                    _game.managerShout();
                    task = new OlympiadGameTask(_game, BattleStatus.Started, 60, 1000);
                    break;
                }
                case Started: {
                    _game.setState(1);
                    _game.preparePlayers();
                    _game.addBuffers();
                    _game.broadcastPacket(new SystemMessage(SystemMsg.THE_MATCH_WILL_START_IN_S1_SECONDS).addNumber(_count), true, true);
                    task = new OlympiadGameTask(_game, BattleStatus.Heal, 55, 10000);
                    if (OlympiadConfig.addCustomEffect)
                        _game.getAllParticipants().forEach(p -> p.addSkill(SkillTable.getInstance().getSkillEntry(90099, 1), false));
                    break;
                }
                case Heal: {
                    _game.heal();
                    task = new OlympiadGameTask(_game, BattleStatus.CountDown, 50, 100);
                    break;
                }
                case CountDown: {
                    _game.broadcastPacket(new SystemMessage(SystemMsg.THE_MATCH_WILL_START_IN_S1_SECONDS).addNumber(_count), true, true);
                    switch (_count) {
                        case 50:
                            task = new OlympiadGameTask(_game, BattleStatus.CountDown, 40, 10000);
                            break;
                        case 40:
                            task = new OlympiadGameTask(_game, BattleStatus.CountDown, 30, 10000);
                            break;
                        case 30:
                            task = new OlympiadGameTask(_game, BattleStatus.CountDown, 20, 10000);
                            break;
                        case 20:
                            task = new OlympiadGameTask(_game, BattleStatus.CountDown, 10, 10000);
                            break;
                        case 10:
                            _game.openDoors();
                            task = new OlympiadGameTask(_game, BattleStatus.CountDown, 5, 5000);
                            break;
                        case 5:
                            task = new OlympiadGameTask(_game, BattleStatus.CountDown, 4, 1000);
                            break;
                        case 4:
                            task = new OlympiadGameTask(_game, BattleStatus.CountDown, 3, 1000);
                            break;
                        case 3:
                            task = new OlympiadGameTask(_game, BattleStatus.CountDown, 2, 1000);
                            break;
                        case 2:
                            task = new OlympiadGameTask(_game, BattleStatus.CountDown, 1, 1000);
                            break;
                        case 1:
                            task = new OlympiadGameTask(_game, BattleStatus.StartComp, 0, 1000);
                            break;
                    }
                    break;
                }
                case StartComp: {
                    _game.deleteBuffers();
                    _game.setState(2);
                    _game.broadcastPacket(SystemMsg.THE_MATCH_HAS_STARTED, true, true);
                    _game.broadcastInfo(null, null, false);
                    task = new OlympiadGameTask(_game, BattleStatus.InComp, 120, 180000); // 300 total
                    break;
                }
                case InComp: {
                    if (_game.getState() == 0) // game finished
                    {
                        return;
                    }
                    _game.broadcastPacket(new SystemMessage(SystemMsg.THE_GAME_WILL_END_IN_S1_SECONDS_).addNumber(_count), true, true);
                    switch (_count) {
                        case 120:
                            task = new OlympiadGameTask(_game, BattleStatus.InComp, 60, 60000);
                            break;
                        case 60:
                            task = new OlympiadGameTask(_game, BattleStatus.InComp, 30, 30000);
                            break;
                        case 30:
                            task = new OlympiadGameTask(_game, BattleStatus.InComp, 10, 20000);
                            break;
                        case 10:
                            task = new OlympiadGameTask(_game, BattleStatus.InComp, 5, 5000);
                            break;
                        case 5:
                            task = new OlympiadGameTask(_game, BattleStatus.ValidateWinner, 0, 5000);
                            break;
                    }
                    break;
                }
                case ValidateWinner: {
                    try {
                        _game.validateWinner(_count > 0);
                    } catch (Exception e) {
                        _log.error("", e);
                    }
                    task = new OlympiadGameTask(_game, BattleStatus.PortBack, 20, 100);
                    break;
                }
                case PortBack: {
                    _game.broadcastPacket(new SystemMessage(SystemMsg.YOU_WILL_BE_MOVED_BACK_TO_TOWN_IN_S1_SECONDS).addNumber(_count), true, false);
                    switch (_count) {
                        case 20:
                            task = new OlympiadGameTask(_game, BattleStatus.PortBack, 10, 10000);
                            break;
                        case 10:
                            task = new OlympiadGameTask(_game, BattleStatus.PortBack, 5, 5000);
                            break;
                        case 5:
                            task = new OlympiadGameTask(_game, BattleStatus.PortBack, 4, 1000);
                            break;
                        case 4:
                            task = new OlympiadGameTask(_game, BattleStatus.PortBack, 3, 1000);
                            break;
                        case 3:
                            task = new OlympiadGameTask(_game, BattleStatus.PortBack, 2, 1000);
                            break;
                        case 2:
                            task = new OlympiadGameTask(_game, BattleStatus.PortBack, 1, 1000);
                            break;
                        case 1:
                            task = new OlympiadGameTask(_game, BattleStatus.Ending, 0, 1000);
                            break;
                    }
                    break;
                }
                case Ending: {
                    if (OlympiadConfig.addCustomEffect)
                        _game.getAllParticipants().forEach(p -> p.removeSkill(90099, false));
                    _game.collapse();
                    _terminated = true;
                    if (Olympiad._manager != null) {
                        Olympiad._manager.freeOlympiadInstance(_game.getId());
                    }
                    return;
                }
            }

            if (task == null) {
                Log.add("task == null for game " + gameId, "olympiad");
                Thread.dumpStack();
                _game.endGame(1, true);
                return;
            }

            _game.sheduleTask(task);
        } catch (Exception e) {
            _log.error("", e);
            _game.endGame(1, true);
        }
    }
}