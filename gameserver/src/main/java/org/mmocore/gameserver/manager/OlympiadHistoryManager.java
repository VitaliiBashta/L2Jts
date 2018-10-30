package org.mmocore.gameserver.manager;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.database.dao.impl.OlympiadHistoryDAO;
import org.mmocore.gameserver.model.entity.Hero;
import org.mmocore.gameserver.model.entity.olympiad.OlympiadHistory;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.utils.HtmlUtils;
import org.mmocore.gameserver.utils.TimeUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author VISTALL
 * @date 20:32/02.05.2011
 */
public class OlympiadHistoryManager {
    private final Map<Integer, List<OlympiadHistory>> historyNew = new ConcurrentHashMap<>();
    private final Map<Integer, List<OlympiadHistory>> historyOld = new ConcurrentHashMap<>();

    private OlympiadHistoryManager() {
        final Map<Boolean, List<OlympiadHistory>> historyList = OlympiadHistoryDAO.getInstance().select();
        for (final Map.Entry<Boolean, List<OlympiadHistory>> entry : historyList.entrySet()) {
            for (final OlympiadHistory history : entry.getValue()) {
                addHistory(entry.getKey(), history);
            }
        }
    }

    public static OlympiadHistoryManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Старую за преведущих 2 месяца удаляет, а за преведущий
     */
    public void switchData() {
        historyOld.clear();

        historyOld.putAll(historyNew);

        historyNew.clear();

        OlympiadHistoryDAO.getInstance().switchData();
    }

    public void saveHistory(final OlympiadHistory history) {
        addHistory(false, history);

        OlympiadHistoryDAO.getInstance().insert(history);
    }

    public void addHistory(final boolean old, final OlympiadHistory history) {
        final Map<Integer, List<OlympiadHistory>> map = old ? historyOld : historyNew;

        addHistory0(map, history.getObjectId1(), history);
        addHistory0(map, history.getObjectId2(), history);
    }

    private void addHistory0(final Map<Integer, List<OlympiadHistory>> map, final int objectId, final OlympiadHistory history) {
        List<OlympiadHistory> historySet = map.get(objectId);
        if (historySet == null) {
            map.put(objectId, historySet = new CopyOnWriteArrayList<>());
        }

        historySet.add(history);
    }

    public void showHistory(final Player player, final int targetClassId, final int page) {
        final int perpage = 15;

        final Map.Entry<Integer, StatsSet> entry = Hero.getInstance().getHeroStats(targetClassId);
        if (entry == null) {
            return;
        }

        List<OlympiadHistory> historyList = historyOld.get(entry.getKey());
        if (historyList == null) {
            historyList = Collections.emptyList();
        }

        final HtmlMessage html = new HtmlMessage(5);
        html.setFile("olympiad/monument_hero_history.htm");

        int allStatWinner = 0;
        int allStatLoss = 0;
        int allStatTie = 0;
        for (final OlympiadHistory h : historyList) {
            if (h.getGameStatus() == 0) {
                allStatTie++;
            } else {
                final int team = entry.getKey() == h.getObjectId1() ? 1 : 2;
                if (h.getGameStatus() == team) {
                    allStatWinner++;
                } else {
                    allStatLoss++;
                }
            }
        }
        html.replace("%wins%", String.valueOf(allStatWinner));
        html.replace("%ties%", String.valueOf(allStatTie));
        html.replace("%losses%", String.valueOf(allStatLoss));

        final int min = perpage * (page - 1);
        final int max = perpage * page;

        int currentWinner = 0;
        int currentLoss = 0;
        int currentTie = 0;

        final StringBuilder b = new StringBuilder(500);

        for (int i = 0; i < historyList.size(); i++) {
            final OlympiadHistory history = historyList.get(i);
            if (history.getGameStatus() == 0) {
                currentTie++;
            } else {
                final int team = entry.getKey() == history.getObjectId1() ? 1 : 2;
                if (history.getGameStatus() == team) {
                    currentWinner++;
                } else {
                    currentLoss++;
                }
            }

            if (i < min) {
                continue;
            }

            if (i >= max) {
                break;
            }

            b.append("<tr><td>");

            final int team = history.getObjectId1() == entry.getKey() ? 1 : 2;
            String list = null;
            if (history.getGameStatus() == 0) {
                list = HtmCache.getInstance().getHtml("olympiad/monument_hero_history_list_draw.htm", player);
            } else if (team == history.getGameStatus()) {
                list = HtmCache.getInstance().getHtml("olympiad/monument_hero_history_list_victory.htm", player);
            } else {
                list = HtmCache.getInstance().getHtml("olympiad/monument_hero_history_list_loss.htm", player);
            }

            final StrBuilder sb = new StrBuilder(list);
            sb.replaceAll("%classId%", String.valueOf(team == 1 ? history.getClassId2() : history.getClassId1()));
            sb.replaceAll("%name%", team == 1 ? history.getName2() : history.getName1());
            sb.replaceAll("%date%", TimeUtils.dateTimeFormat(history.getGameStartTime()));
            sb.replaceAll("%time%", String.format("%02d:%02d", history.getGameTime() / 60, history.getGameTime() % 60));
            sb.replaceAll("%victory_count%", String.valueOf(currentWinner));
            sb.replaceAll("%tie_count%", String.valueOf(currentTie));
            sb.replaceAll("%loss_count%", String.valueOf(currentLoss));
            b.append(sb);

            b.append("</td></tr");
        }

        if (min > 0) {
            html.replace("%buttprev%", HtmlUtils.PREV_BUTTON);
            html.replace("%prev_bypass%", "_match?class=" + targetClassId + "&page=" + (page - 1));
        } else {
            html.replace("%buttprev%", StringUtils.EMPTY);
        }

        if (historyList.size() > max) {
            html.replace("%buttnext%", HtmlUtils.NEXT_BUTTON);
            html.replace("%next_bypass%", "_match?class=" + targetClassId + "&page=" + (page + 1));
        } else {
            html.replace("%buttnext%", StringUtils.EMPTY);
        }

        html.replace("%list%", b.toString());

        player.sendPacket(html);
    }

    private static class LazyHolder {
        private static final OlympiadHistoryManager INSTANCE = new OlympiadHistoryManager();
    }
}
