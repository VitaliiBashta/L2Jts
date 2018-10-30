package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.ExtConfig;
import org.mmocore.gameserver.database.dao.impl.BotReportDAO;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.manager.BotReportManager;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.bot_punish.BotPunish.Punish;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.AutoBan;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.text.DateFormat;

public class AdminBotReport implements IAdminCommandHandler {
    private static void sendBotPage(final Player activeChar) {
        final StringBuilder tb = new StringBuilder("<html>");
        tb.append("<table width=260><tr>");
        tb.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        tb.append("<td width=180><center>Bot Report's info</center></td>");
        tb.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        tb.append("</tr></table>");
        tb.append("<br><br>");
        tb.append("<title>Unread Bot List</title><body><center>");
        tb.append("Here's a list of the current <font color=LEVEL>unread</font><br1>bots!<br>");

        for (final int i : BotReportManager.getInstance().getUnread().keySet()) {
            tb.append("<a action=\"bypass -h admin_readbot ").append(i).append("\">Ticket #").append(i).append("</a><br1>");
        }
        tb.append("</center></body></html>");

        final HtmlMessage nhm = new HtmlMessage(5);
        nhm.setHtml(tb.toString());
        activeChar.sendPacket(nhm);
    }

    private static void sendBotInfoPage(final Player activeChar, final int botId) {
        final String[] report = BotReportManager.getInstance().getUnread().get(botId);
        final StringBuilder tb = new StringBuilder();

        tb.append("<html><title>Bot #").append(botId).append("</title><body><center><br>");
        tb.append("- Bot report ticket Id: <font color=FF0000>").append(botId).append("</font><br>");
        tb.append("- Player reported: <font color=FF0000>").append(report[0]).append("</font><br>");
        tb.append("- Reported by: <font color=FF0000>").append(report[1]).append("</font><br>");
        tb.append("- Date: <font color=FF0000>").append(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(Long.parseLong(report[2]))).append("</font><br>");
        tb.append("<a action=\"bypass -h admin_markbotreaded ").append(botId).append("\">Mark Report as Read</a>");
        tb.append("<a action=\"bypass -h admin_punish_bot ").append(report[0]).append("\">Punish ").append(report[0]).append("</a>");
        tb.append("<a action=\"bypass -h admin_checkbots\">Go Back to bot list</a>");
        tb.append("</center></body></html>");

        final HtmlMessage nhm = new HtmlMessage(5);
        nhm.setHtml(tb.toString());
        activeChar.sendPacket(nhm);
    }

    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        if (!ExtConfig.EX_ENABLE_AUTO_HUNTING_REPORT) {
            activeChar.sendAdminMessage("Bot reporting is not enabled!");
            return false;
        }

        if (!activeChar.getPlayerAccess().CanBan)
            return false;

        final Commands command = (Commands) comm;

        final String[] ids = fullString.split(" ");

        switch (command) {
            case admin_checkbots:
                sendBotPage(activeChar);
                break;
            case admin_readbot:
                sendBotInfoPage(activeChar, Integer.parseInt(ids[1]));
                break;
            case admin_markbotreaded: {
                try {
                    BotReportManager.getInstance().markAsRead(Integer.parseInt(wordList[1]));
                    sendBotPage(activeChar);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case admin_punish_bot: {
                activeChar.sendAdminMessage("Usage: //punish_bot <charName>");

                if (wordList != null) {
                    final Player target = GameObjectsStorage.getPlayer(wordList[1]);
                    if (target != null) {
                        synchronized (target) {
                            int punishLevel = 0;
                            try {
                                punishLevel = BotReportManager.getInstance().getPlayerReportsCount(target);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            // By System Message guess:
                            // Reported 1 time = 10 mins chat ban
                            // Reported 2 times = 60 mins w/o join pt
                            // Reported 3 times = 120 mins w/o join pt
                            // Reported 4 times = 180 mins w/o join pt
                            // Reported 5 times = 120 mins w/o move
                            // Reported 6 times = 180 mins w/o move
                            // Reported 7 times = 120 mins w/o any action
                            switch (punishLevel) {
                                case 1:
                                    target.doCast(SkillTable.getInstance().getSkillEntry(6038, 1), activeChar, true);
                                    target.getBotPunishComponent().setPunishDueBotting(Punish.CHATBAN, 10);
                                    target.sendPacket(SystemMsg.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_CHATTING_WILL_BE_BLOCKED_FOR_10_MINUTES);
                                    break;
                                case 2:
                                    target.doCast(SkillTable.getInstance().getSkillEntry(6039, 1), activeChar, true);
                                    target.getBotPunishComponent().setPunishDueBotting(Punish.PARTYBAN, 60);
                                    target.sendPacket(SystemMsg.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_PARTY_PARTICIPATION_WILL_BE_BLOCKED_FOR_60_MINUTES);
                                    break;
                                case 3:
                                    target.doCast(SkillTable.getInstance().getSkillEntry(6039, 1), activeChar, true);
                                    target.getBotPunishComponent().setPunishDueBotting(Punish.PARTYBAN, 120);
                                    target.sendPacket(SystemMsg.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_PARTY_PARTICIPATION_WILL_BE_BLOCKED_FOR_120_MINUTES);
                                    break;
                                case 4:
                                    target.doCast(SkillTable.getInstance().getSkillEntry(6039, 1), activeChar, true);
                                    target.getBotPunishComponent().setPunishDueBotting(Punish.PARTYBAN, 180);
                                    target.sendPacket(SystemMsg.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_PARTY_PARTICIPATION_WILL_BE_BLOCKED_FOR_180_MINUTES);
                                    break;
                                case 5:
                                    target.doCast(SkillTable.getInstance().getSkillEntry(6057, 1), activeChar, true);
                                    target.getBotPunishComponent().setPunishDueBotting(Punish.MOVEBAN, 120);
                                    target.sendPacket(SystemMsg.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_MOVEMENT_IS_PROHIBITED_FOR_120_MINUTES);
                                    break;
                                case 6:
                                    target.doCast(SkillTable.getInstance().getSkillEntry(6055, 1), activeChar, true);
                                    target.getBotPunishComponent().setPunishDueBotting(Punish.ACTIONBAN, 120);
                                    target.sendPacket(SystemMsg.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_WILL_BE_RESTRICTED_FOR_120_MINUTES);
                                    break;
                                case 7:
                                    target.doCast(SkillTable.getInstance().getSkillEntry(6056, 1), activeChar, true);
                                    target.getBotPunishComponent().setPunishDueBotting(Punish.ACTIONBAN, 180);
                                    target.sendPacket(SystemMsg.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_WILL_BE_RESTRICTED_FOR_180_MINUTES);
                                    break;
                                case 8:
                                    target.doCast(SkillTable.getInstance().getSkillEntry(6040, 1), activeChar, true);
                                    target.getBotPunishComponent().setPunishDueBotting(Punish.ATTACKBAN, 180);
                                    target.sendPacket(SystemMsg.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_WILL_BE_RESTRICTED_FOR_180_MINUTES_, SystemMsg.YOU_HAVE_BEEN_BLOCKED_DUE_TO_VERIFICATION_THAT_YOU_ARE_USING_A_THIRD_PARTY_PROGRAM);

                            }
                            if (punishLevel != 0) {
                                BotReportDAO.getInstance().introduceNewPunishedBotAndClear(target);
                                activeChar.sendAdminMessage(target.getName() + " has been punished");
                                if (punishLevel > 15) {
                                    target.sendPacket(SystemMsg.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_YOUR_CONNECTION_HAS_BEEN_ENDED);
                                    ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                                        @Override
                                        public void runImpl() {
                                            target.setAccessLevel(-100);
                                            AutoBan.banPlayer(target, -1, "More used illegal programm", activeChar.getName());
                                            target.kick();
                                        }
                                    }, 1000L);
                                }
                            }
                        }
                    } else
                        activeChar.sendAdminMessage("Your target doesnt exist!");
                }
            }
        }
        return true;
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_checkbots,
        admin_readbot,
        admin_markbotreaded,
        admin_punish_bot
    }
}