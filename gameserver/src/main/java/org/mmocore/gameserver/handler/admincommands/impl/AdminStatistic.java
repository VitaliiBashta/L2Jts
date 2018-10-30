package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.manager.StatisticManager;
import org.mmocore.gameserver.object.Player;

import java.util.Date;
import java.util.Map;

/**
 * Created by Hack
 * Date: 20.09.2016 6:03
 */
public class AdminStatistic implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar) throws InstantiationException, IllegalAccessException {
        if (!activeChar.getPlayerAccess().Menu)
            return false;
        switch ((Command) comm) {
            case admin_statistic:
                String page = HtmCache.getInstance().getHtml("admin/statistic.htm", activeChar);
                page = preparePage(page);
                Functions.show(page, activeChar, null);
                break;
        }
        return true;
    }

    private String preparePage(String page) {
        page = setOnline(page);
        page = setDayStatistic(page);
        return page;
    }

    private String setOnline(String page) {
        return replaceByMap(StatisticManager.getInstance().getCurrentStatistic(), page);
    }

    private String setDayStatistic(String page) {
        return replaceByMap(StatisticManager.getInstance().getDayStatistic(), page);
    }

    private <N extends Number> String replaceByMap(Map<String, N> map, String page) {
        for (Map.Entry<String, N> entry : map.entrySet()) {
            if (entry.getKey().equals("maxOnlineTime"))
                page = page.replaceFirst("%" + entry.getKey() + "%", new Date(entry.getValue().longValue()) + "");
            else
                page = page.replaceFirst("%" + entry.getKey() + "%", entry.getValue() + "");
        }
        return page;
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Command.values();
    }

    @Override
    public String[] getAdminCommandString() {
        return null;
    }

    private enum Command {
        admin_statistic
    }
}
