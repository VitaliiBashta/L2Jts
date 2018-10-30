package org.mmocore.gameserver.handler.admincommands.impl;


import org.mmocore.gameserver.data.xml.holder.AnnouncementsHolder;
import org.mmocore.gameserver.data.xml.parser.AnnouncementsParser;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.model.Announcement;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.AnnouncementUtils;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.util.List;


/**
 * This class handles following admin commands: - announce text = announces text
 * to all players - list_announcements = show menu - reload_announcements =
 * reloads announcements from txt file - announce_announcements = announce all
 * stored announcements to all players - add_announcement text = adds text to
 * startup announcements - del_announcement id = deletes announcement with
 * respective id
 */
public class AdminAnnouncements implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanAnnounce) {
            return false;
        }

        switch (command) {
            case admin_list_announcements:
                listAnnouncements(activeChar);
                break;
            case admin_announce_menu:
                AnnouncementUtils.announceToAll(fullString.substring(20));
                listAnnouncements(activeChar);
                break;
            case admin_announce_announcements:
                for (final Player player : GameObjectsStorage.getPlayers()) {
                    AnnouncementUtils.showAnnouncements(player);
                }
                listAnnouncements(activeChar);
                break;
            case admin_add_announcement:
                if (wordList.length < 6) {
                    return false;
                }

                try {
                    boolean critical = Boolean.parseBoolean(wordList[1]);
                    boolean auto = Boolean.parseBoolean(wordList[2]);
                    int initial_delay = Integer.parseInt(wordList[3]);
                    int delay = Integer.parseInt(wordList[4]);
                    int limit = Integer.parseInt(wordList[5]);
                    StringBuilder message = new StringBuilder();
                    for (int i = 6; i < wordList.length; i++) {
                        if (i == 6) {
                            message.append(wordList[i]);
                        } else {
                            message.append(" " + wordList[i]);
                        }
                    }
                    AnnouncementUtils.addAnnouncement(message.toString(), critical, auto, initial_delay, delay, limit);
                    listAnnouncements(activeChar);
                } catch (Exception e) {
                    activeChar.sendDebugMessage("ЧТО ТВОРИШЬ ЧТО ТВОРИШЬ!!!!");
                }
                break;

            case admin_del_announcement:
				/*
				if(wordList.length != 2)
				{
					return false;
				}
				*/
                final int val = Integer.parseInt(wordList[1]);
                AnnouncementUtils.deleteAnnouncement(val);
                listAnnouncements(activeChar);
                break;
            case admin_announce:
                AnnouncementUtils.announceToAll(fullString.substring(15));
                break;
            case admin_a:
                AnnouncementUtils.announceToAll(fullString.substring(8));
                break;
            case admin_crit_announce:
            case admin_c:
                if (wordList.length < 2) {
                    return false;
                }
                AnnouncementUtils.announceToAll(activeChar.getName() + ": " + fullString.replaceFirst("admin_crit_announce ", "").replaceFirst("admin_c ", ""), ChatType.CRITICAL_ANNOUNCE);
                break;
            case admin_gm_announce:
                AnnouncementUtils.announceToAll(fullString.substring(18), ChatType.GM);
            case admin_toscreen:
            case admin_s:
                if (wordList.length < 2) {
                    return false;
                }
                final String text = activeChar.getName() + ": " + fullString.replaceFirst("admin_toscreen ", "").replaceFirst("admin_s ", "");
                final int time = 3000 + text.length() * 100; // 3 секунды + 100мс на символ
                final ExShowScreenMessage sm = new ExShowScreenMessage(NpcString.NONE, time, ScreenMessageAlign.TOP_CENTER, text.length() < 64, text);
                for (final Player player : GameObjectsStorage.getPlayers()) {
                    player.sendPacket(sm);
                }
                break;
            case admin_reload_announcements:
                AnnouncementsParser.getInstance().reload();
                listAnnouncements(activeChar);
                activeChar.sendAdminMessage("Announcements reloaded.");
                break;
            default:
                break;
        }

        return true;
    }

    public void listAnnouncements(final Player activeChar) {
        final HtmlMessage adminReply = new HtmlMessage(5);

        final StringBuilder replyMSG = new StringBuilder("<html><body>");
        replyMSG.append("<table width=260><tr>");
        replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td width=180><center>Announcement Menu</center></td>");
        replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("</tr></table>");
        replyMSG.append("<br><br>");
        replyMSG.append("<center>Add or announce a new announcement:</center>");
        replyMSG.append("<center><multiedit var=\"new_announcement\" width=240 height=30></center><br>");
        replyMSG.append("<center><font color=\"ffff00\">critical</font>(true = critical,false = normal)<combobox width=60 height=10 var=\"critical\" list=\"false;true\"> </center><br>");
        replyMSG.append("<center><font color=\"ffff00\">auto</font>(true = auto,false = on player login)<combobox width=60 height=10 var=\"auto\" list=\"false;true\"> </center><br>");
        replyMSG.append("<center><font color=\"ffff00\">initial_delay</font>(<font color=\"FF0000\">used only if auto=true</font>)<edit var=\"initial_delay\" width=40 height=10></center><br>");
        replyMSG.append("<center><font color=\"ffff00\">delay</font>(<font color=\"FF0000\">used only if auto=true</font> ; value in seconds)<edit var=\"delay\" width=40 height=10></center><br>");
        replyMSG.append("<center><font color=\"ffff00\">limit</font>(<font color=\"FF0000\">used only if auto=true</font> ; -1 = no limit)<edit var=\"limit\" width=40 height=10></center><br>");
        replyMSG.append("<center><table><tr><td>");
        replyMSG.append("<button value=\"Add\" action=\"bypass -h admin_add_announcement $critical $auto $initial_delay $delay $limit $new_announcement\" width=60 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>");
        replyMSG.append("<button value=\"Announce\" action=\"bypass -h admin_announce_menu $new_announcement\" width=64 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>");
        replyMSG.append("<button value=\"Reload\" action=\"bypass -h admin_reload_announcements\" width=60 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>");
        replyMSG.append("<button value=\"Broadcast\" action=\"bypass -h admin_announce_announcements\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
        replyMSG.append("</td></tr></table></center>");
        replyMSG.append("<br>");
        final List<Announcement> announcements = AnnouncementsHolder.getInstance().getFullAnnouncement();
        for (int i = 0; i < announcements.size(); i++) {
            final Announcement announce = announcements.get(i);
            replyMSG.append("<table width=260><tr><td width=180>").append(announce.getMessage()).append("</td><td width=40>")
                    .append(announce.getDelay()).append("</td><<td ").append("width=40>");
            replyMSG.append("<button value=\"Delete\" action=\"bypass -h admin_del_announcement ").append(i).append(
                    "\" width=60 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr></table>");
        }

        replyMSG.append("</body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);
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
        admin_list_announcements,
        admin_announce_announcements,
        admin_add_announcement,
        admin_del_announcement,
        admin_announce,
        admin_a,
        admin_announce_menu,
        admin_crit_announce,
        admin_c,
        admin_toscreen,
        admin_s,
        admin_reload_announcements,
        admin_gm_announce
    }
}