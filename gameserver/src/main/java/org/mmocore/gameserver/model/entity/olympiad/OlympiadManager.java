package org.mmocore.gameserver.model.entity.olympiad;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.OlympiadConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class OlympiadManager extends RunnableImpl {
    private static final Logger _log = LoggerFactory.getLogger(OlympiadManager.class);

    private final Map<Integer, OlympiadGame> _olympiadInstances = new ConcurrentHashMap<>();

    private static void removeOpponent(final int objectId) {
        Olympiad.classBasedRegisters.entrySet().forEach(entry -> {
            if (entry.getValue().contains(objectId)) {
                Olympiad.classBasedRegisters.remove(entry.getKey(), entry.getValue());
            }
        });
        Olympiad.nonClassBasedRegisters.remove(Integer.valueOf(objectId));
        Olympiad.teamBasedRegisters.entrySet().forEach(entry -> {
            if (entry.getValue().contains(objectId)) {
                Olympiad.teamBasedRegisters.remove(entry.getKey(), entry.getValue());
            }
        });
        RegisteredPlayerInfo info = Olympiad.getRegisteredPlayerInfo(objectId);
        if (info != null) {
            Olympiad.registeredPlayersInfo.remove(info);
        }
    }

    public void sleep(final long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }

    @Override
    public void runImpl() throws Exception {
        if (Olympiad.isOlympiadEnd()) {
            return;
        }

        while (Olympiad.inCompPeriod()) {
            if (Olympiad.nobles.isEmpty()) {
                sleep(60000);
                continue;
            }

            while (Olympiad.inCompPeriod()) {
                // Подготовка и запуск внеклассовых боев
                if (Olympiad.nonClassBasedRegisters.size() >= OlympiadConfig.NONCLASS_GAME_MIN) {
                    prepareBattles(CompType.NON_CLASSED, Olympiad.nonClassBasedRegisters);
                }

                // Подготовка и запуск классовых боев
                Olympiad.classBasedRegisters.entrySet().stream()
                        .filter(entry -> entry.getValue().size() >= OlympiadConfig.CLASS_GAME_MIN).forEach(entry -> {
                    prepareBattles(CompType.CLASSED, entry.getValue());
                });

                // Подготовка и запуск командных боев
                if (Olympiad.teamBasedRegisters.size() >= OlympiadConfig.TEAM_GAME_MIN) {
                    prepareTeamBattles(CompType.TEAM, new ConcurrentLinkedQueue<>(Olympiad.teamBasedRegisters.values()));
                }

                sleep(30000);
            }

            sleep(30000);
        }

        Olympiad.classBasedRegisters.clear();
        Olympiad.nonClassBasedRegisters.clear();
        Olympiad.teamBasedRegisters.clear();
        Olympiad.registeredPlayersInfo.clear();

        // when comp time finish wait for all games terminated before execute the cleanup code
        boolean allGamesTerminated = false;

        // wait for all games terminated
        while (!allGamesTerminated) {
            sleep(30000);

            if (_olympiadInstances.isEmpty()) {
                break;
            }

            allGamesTerminated = true;
            for (final OlympiadGame game : _olympiadInstances.values()) {
                if (game.getTask() != null && !game.getTask().isTerminated()) {
                    allGamesTerminated = false;
                }
            }
        }

        _olympiadInstances.clear();
    }

    private void prepareBattles(final CompType type, final Collection<Integer> list) {
        final NobleSelector<Integer> selector = new NobleSelector<>(list.size());
        for (final Integer noble : list) {
            if (noble != null) {
                selector.add(noble, Olympiad.getNoblePoints(noble));
            }
        }

        for (int i = 0; i < Olympiad.STADIUMS.length; i++) {
            try {
                if (!Olympiad.STADIUMS[i].isFreeToUse()) {
                    continue;
                }
                if (selector.size() < type.getMinSize()) {
                    break;
                }

                final OlympiadGame game = new OlympiadGame(i, type, nextOpponents(selector, type));
                game.sheduleTask(new OlympiadGameTask(game, BattleStatus.Begining, 0, 1));

                _olympiadInstances.put(i, game);

                Olympiad.STADIUMS[i].setStadiaBusy();
            } catch (Exception e) {
                _log.error("", e);
            }
        }
    }

    private void prepareTeamBattles(final CompType type, final Collection<Collection<Integer>> list) {
        for (int i = 0; i < Olympiad.STADIUMS.length; i++) {
            try {
                if (!Olympiad.STADIUMS[i].isFreeToUse()) {
                    continue;
                }
                if (list.size() < type.getMinSize()) {
                    break;
                }

                final List<Integer> nextOpponents = nextTeamOpponents(list, type);
                if (nextOpponents == null) {
                    break;
                }

                final OlympiadGame game = new OlympiadGame(i, type, nextOpponents);
                game.sheduleTask(new OlympiadGameTask(game, BattleStatus.Begining, 0, 1));

                _olympiadInstances.put(i, game);

                Olympiad.STADIUMS[i].setStadiaBusy();
            } catch (Exception e) {
                _log.error("", e);
            }
        }
    }

    public void freeOlympiadInstance(final int index) {
        _olympiadInstances.remove(index);
        Olympiad.STADIUMS[index].setStadiaFree();
    }

    public OlympiadGame getOlympiadInstance(final int index) {
        return _olympiadInstances.get(index);
    }

    public Map<Integer, OlympiadGame> getOlympiadGames() {
        return _olympiadInstances;
    }

    private List<Integer> nextOpponents(final NobleSelector<Integer> selector, final CompType type) {
        final List<Integer> opponents = new ArrayList<>();
        Integer noble;

        selector.reset();
        for (int i = 0; i < type.getMinSize(); i++) {
            noble = selector.select();
            if (noble == null) // DS: error handling ?
            {
                break;
            }
            opponents.add(noble);
            removeOpponent(noble);
        }

        return opponents;
    }

    private List<Integer> nextTeamOpponents(final Collection<Collection<Integer>> list, final CompType type) {
        if (list.isEmpty()) {
            return null;
        }
        final List<Integer> opponents = new CopyOnWriteArrayList<>();
        final List<Collection<Integer>> a = new ArrayList<>();
        a.addAll(list);

        for (int i = 0; i < type.getMinSize(); i++) {
            if (a.size() < 1) {
                continue;
            }
            final Collection<Integer> team = a.remove(Rnd.get(a.size()));
            if (team.size() == 3) {
                for (final Integer noble : team) {
                    opponents.add(noble);
                    removeOpponent(noble);
                }
            } else {
                for (final Integer noble : team) {
                    removeOpponent(noble);
                }
                i--;
            }

            list.remove(team);
        }

        return opponents;
    }
}