package org.mmocore.gameserver.manager;

import gnu.trove.map.TIntLongMap;
import gnu.trove.map.hash.TIntLongHashMap;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.database.dao.impl.BotReportDAO;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class BotReportManager {
    private static final Logger logger = LoggerFactory.getLogger(BotReportManager.class);
    private static final Map<Integer, List<Player>> reportedCount = new HashMap<>();
    private static final TIntLongMap lockedReporters = new TIntLongHashMap();
    private static final Set<String> lockedIps = new HashSet<>();
    private static final Set<String> lockedAccounts = new HashSet<>();
    private static Map<Integer, String[]> unread;

    private BotReportManager() {
        loadUnread();
    }

    public static BotReportManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Will save the report in database
     *
     * @param reported (the Player who was reported)
     * @param reporter (the Player who reported the bot)
     */
    public synchronized void reportBot(final Player reported, final Player reporter) {
        if (reported == null || !reported.isOnline()) {
            reporter.sendPacket(SystemMsg.YOU_CANNOT_REPORT_THIS_PERSON_AGAIN_AT_THIS_TIME);
            return;
        }

        if (!getReportedCount().containsKey(reporter.getObjectId())) {
            final List<Player> p = new ArrayList<>();
            p.add(reported);
            getReportedCount().put(reporter.getObjectId(), p);
        } else {
            if (getReportedCount().get(reporter.getObjectId()).contains(reported)) {
                reporter.sendPacket(SystemMsg.YOU_CANNOT_REPORT_THIS_PERSON_AGAIN_AT_THIS_TIME);
                return;
            }
            getReportedCount().get(reporter.getObjectId()).add(reported);
        }

        BotReportDAO.getInstance().saveReport(reported, reporter);

        lockedReporters.put(reporter.getObjectId(), System.currentTimeMillis());
        lockedIps.add(reporter.getIP());
        lockedAccounts.add(reporter.getAccountName());

        final SystemMessage sm = new SystemMessage(SystemMsg.C1_REPORTED_AS_BOT);
        sm.addName(reported);
        reporter.sendPacket(sm);
    }

    /**
     * Will load the data from all unreaded reports (used to load reports
     * in a window for admins/GMs)
     *
     * @return a FastMap<Integer, String[]> (Integer - report id, String[] - reported name, report name, date)
     */
    private void loadUnread() {
        if (unread == null)
            unread = new HashMap<>();
        BotReportDAO.getInstance().loadUnread();
        logger.info("Bot Report Manager: Loaded " + unread.size() + " unreaded reports");
    }

    /**
     * Return a FastMap holding all the reports data
     * to be viewed by any GM
     *
     * @return _unread
     */
    public Map<Integer, String[]> getUnread() {
        return unread;
    }

    public Map<Integer, List<Player>> getReportedCount() {
        return reportedCount;
    }

    /**
     * Marks a reported bot as readed (from admin menu)
     *
     * @param id (the report id)
     */
    public void markAsRead(final int id) {
        BotReportDAO.getInstance().saveUnread(id);
    }

    /**
     * Returns the number of times the player has been reported
     *
     * @param reported
     * @return int
     */
    public int getPlayerReportsCount(final Player reported) {
        return BotReportDAO.getInstance().requestPlayerReportsCount(reported);
    }

    /**
     * Will save the punish being suffered to player in database
     * (at player logs out), to be restored next time players enter
     * in server
     *
     * @param
     */
    public void savePlayerPunish(final Player player) {
        BotReportDAO.getInstance().savePunish(player);
    }

    /**
     * Retail report restrictions (Validates the player - reporter relationship)
     *
     * @param reported (the reported bot)
     * @return
     */
    public boolean validateBot(final Player reported, final Player reporter) {
        if (reported == null || reporter == null || reporter.getObjectId() == reported.getObjectId()) {
            return false;
        }

        // Cannot report while reported is inside peace zone, war zone or olympiad
        if (reported.isInPeaceZone() || reported.isInCombatZone() || reported.isInOlympiadMode()) {
            reporter.sendPacket(SystemMsg.THIS_CHARACTER_CANNOT_MAKE_A_REPORT);
            return false;
        }
        // Cannot report if reported and reporter are in war
        if (reported.getClan() != null && reporter.getClan() != null) {
            if (reported.getClan().isAtWarWith(reporter.getClanId())) {
                reporter.sendPacket(SystemMsg.YOU_CANNOT_REPORT_WHEN_A_CLAN_WAR_HAS_BEEN_DECLARED);
                return false;
            }
        }
        // Cannot report if the reported didnt earn exp since he logged in
        if (!reported.getBotPunishComponent().hasEarnedExp()) {
            reporter.sendPacket(SystemMsg.YOU_CANNOT_REPORT_A_CHARACTER_WHO_HAS_NOT_ACQUIRED_ANY_EXP);
            return false;
        }
        // Cannot report twice or more a player
        if (reportedCount.containsKey(reporter.getObjectId())) {
            for (final Player p : reportedCount.get(reporter.getObjectId())) {
                if (reported.equals(p)) {
//					reporter.sendPacket(SystemMsg.C1_REPORTED_AS_BOT); // TODO: [Hack] - поправить мессагу
                    reporter.sendMessage("Already reported.");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Retail report restrictions (Validates the reporter state)
     *
     * @param reporter
     * @return
     */
    public synchronized boolean validateReport(final Player reporter, final String name) {
        if (reporter == null) {
            return false;
        }

        // The player has a 30 mins lock before be able to report anyone again
        if (reporter.getBotPunishComponent().getReportsPoints() == 0) {
            reporter.sendPacket(SystemMsg.YOU_HAVE_USED_ALL_AVAILABLE_POINTS_POINTS_ARE_RESET_EVERYDAY_AT_NOON);
            return false;
        }

        // 30 mins must pass before report again
        else if (lockedReporters.containsKey(reporter.getObjectId())) {
            final long delay = (System.currentTimeMillis() - lockedReporters.get(reporter.getObjectId()));
            if (delay <= 1800000) {
                final int left = (int) (1800000 - delay) / 60000;
                final SystemMessage sm = new SystemMessage(SystemMsg.YOU_CAN_REPORT_IN_S1_MINUTES_S2_REPORT_POINTS_REMAIN_IN_ACCOUNT);
                sm.addNumber(left);
                sm.addNumber(reporter.getBotPunishComponent().getReportsPoints());
                reporter.sendPacket(sm);
                return false;
            } else {
                ThreadPoolManager.getInstance().execute(new ReportClear(reporter));
            }
        }
        // In those 30 mins, the ip which made the first report cannot report again
        else if (lockedIps.contains(reporter.getIP())) {
            reporter.sendPacket(SystemMsg.THIS_CHARACTER_CANNOT_MAKE_A_REPORT_);
            return false;
        }
        // In those 30 mins, the account which made report cannot report again
        else if (lockedAccounts.contains(reporter.getAccountName())) {
            reporter.sendPacket(SystemMsg.THIS_CHARACTER_CANNOT_MAKE_A_REPORT_BECAUSE_ANOTHER_CHARACTER_FROM_THIS_ACCOUNT_HAS_ALREADY_DONE_SO);
            return false;
        }
        // If any clan/ally mate has reported any bot, you cannot report till he releases his lock
        else if (reporter.getClan() != null) {
            for (final int i : lockedReporters.keys()) {
                // Same clan
                final Player p = World.getPlayer(i);
                if (p == null) {
                    continue;
                }

                if (p.getClanId() == reporter.getClanId()) {
                    reporter.sendPacket(SystemMsg.THIS_CHARACTER_CANNOT_MAKE_A_REPORT_);
                    return false;
                }
                // Same ally
                else if (reporter.getClan().getAllyId() != 0) {
                    if (p.getClan() != null && (p.getClan().getAllyId() == reporter.getClan().getAllyId())) {
                        reporter.sendPacket(SystemMsg.THIS_CHARACTER_CANNOT_MAKE_A_REPORT_);
                        return false;
                    }
                }
            }
        }
        reporter.getBotPunishComponent().reduceReportsPoints();
        reporter.sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_USED_A_REPORT_POINT_ON_$C1_YOU_HAVE_$S2_POINTS_REMAINING_ON_THIS_ACCOUNT).addString(name).addNumber(reporter.getBotPunishComponent().getReportsPoints()));
        return true;
    }

    /**
     * Will manage needed actions on enter
     *
     * @param activeChar
     */
    public void onEnter(final Player activeChar) {
        activeChar.getBotPunishComponent().setFirstExp(activeChar.getExp());
        restorePlayerBotPunishment(activeChar);
        activeChar.getBotPunishComponent().requestReportsPoints();
    }

    /**
     * Will retore the player punish on enter
     *
     * @param
     */
    private void restorePlayerBotPunishment(final Player player) {
        BotReportDAO.getInstance().restorePunish(player);
    }

    /**
     * Manages the reporter restriction data clean up
     * to be able to report again
     */
    private static class ReportClear implements Runnable {
        private final Player _reporter;

        private ReportClear(final Player reporter) {
            _reporter = reporter;
        }

        @Override
        public void run() {
            lockedReporters.remove(_reporter.getObjectId());
            lockedIps.remove(_reporter.getNetConnection().getIpAddr());
            lockedAccounts.remove(_reporter.getAccountName());
        }
    }

    private static class LazyHolder {
        private static final BotReportManager INSTANCE = new BotReportManager();
    }
}