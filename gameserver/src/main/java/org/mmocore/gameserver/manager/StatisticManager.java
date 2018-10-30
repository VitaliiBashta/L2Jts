package org.mmocore.gameserver.manager;

import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.listener.actor.player.OnPlayerEnterListener;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.PlayerListenerList;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.util.*;

/**
 * Created by Hack
 * Date: 20.09.2016 6:19
 */
public class StatisticManager {
    private final static StatisticManager instance = new StatisticManager();
    private final long cacheTime = 60_000; // время кеширования страницы. TODO: в конфиг
    private final long dayRefreshTime = 3_600_000; // время обновления дневной статистики (диаграмма онлайна и тд) TODO: в конфиг
    private HashMap<String, Integer> cachedResult;
    private long lastCalculate = 0;
    private Set<Integer> dayPlayers = new HashSet<>();
    private Set<String> dayHwids = new HashSet<>();
    private List<HashMap<String, Integer>> dayStatHolder = new ArrayList<>(); // хранит все мапки результатов дневной статистики
    private List<Long> dayTimeHolder = new ArrayList<>(); // хранит время получения мапки дневной статистики. Соответствие по индексам

    private StatisticManager() {
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new StatCollector(), dayRefreshTime, dayRefreshTime);
        PlayerListenerList.addGlobal(new OnPlayerEnterListener() {
            @Override
            public void onPlayerEnter(Player player) {
                addPlayer(player);
            }
        });
    }

    public static StatisticManager getInstance() {
        return instance;
    }

    public void addPlayer(Player player) {
        dayPlayers.add(player.getObjectId());
        String hwid = player.getNetConnection().getHWID();
        if (hwid != null)
            dayHwids.add(hwid);
    }

    private HashMap<String, Integer> calculate() {
        HashMap<String, Integer> result = new HashMap<>();
        HashSet<String> hwids = new HashSet<>();
        int total = 0,
                fakes = 0,
                offtraders = 0,
                gms = 0;

        for (Player player : GameObjectsStorage.getPlayers()) {
            total++;
            if (player.isInOfflineMode())
                offtraders++;
            if (player.getHwid() != null)
                hwids.add(player.getHwid());
            if (player.isGM())
                gms++;
            if (player.isPhantom())
                fakes++;
        }

        result.put("total", total);
        result.put("fakes", fakes);
        result.put("offtraders", offtraders);
        result.put("real", total - fakes - offtraders);
        result.put("hwids", hwids.size());
        result.put("gms", gms);
        lastCalculate = System.currentTimeMillis();
        cachedResult = result;
        return result;
    }

    public HashMap<String, Long> getDayStatistic() {
        HashMap<String, Long> result = new HashMap<>();
        result.put("players", (long) dayPlayers.size());
        result.put("hwids", (long) dayHwids.size());
        long maxOnlineTime = 0, maxOnline = 0;
        for (int i = 0; i < dayStatHolder.size(); i++) {
            int online = dayStatHolder.get(i).get("total");
            if (online > maxOnline) {
                maxOnline = online;
                maxOnlineTime = dayTimeHolder.get(i);
            }
        }
        result.put("maxOnline", maxOnline);
        result.put("maxOnlineTime", maxOnlineTime);
        return result;
    }

    public HashMap<String, Integer> getCurrentStatistic() {
        if (System.currentTimeMillis() > lastCalculate + cacheTime)
            calculate();
        return cachedResult;
    }

    private class StatCollector implements Runnable {
        @Override
        public void run() {
            dayStatHolder.add(calculate());
            dayTimeHolder.add(System.currentTimeMillis());
        }
    }

}
