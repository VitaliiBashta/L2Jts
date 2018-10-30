package org.mmocore.gameserver.handler.admincommands.impl;

import org.apache.commons.lang3.math.NumberUtils;
import org.mmocore.commons.lang.StatsUtils;
import org.mmocore.gameserver.Shutdown;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.manager.GameTimeManager;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AdminShutdown implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanRestart) {
            return false;
        }

        try {
            switch (command) {
                case admin_server_shutdown:
                    Shutdown.getInstance().schedule(NumberUtils.toInt(wordList[1], -1), Shutdown.SHUTDOWN);
                    break;
                case admin_server_restart:
                    Shutdown.getInstance().schedule(NumberUtils.toInt(wordList[1], -1), Shutdown.RESTART);
                    break;
                case admin_server_abort:
                    Shutdown.getInstance().cancel();
                    break;
            }
        } catch (Exception e) {
            sendHtmlForm(activeChar);
        }

        return true;
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    private void sendHtmlForm(final Player activeChar) {
        final HtmlMessage adminReply = new HtmlMessage(5);

        final int t = GameTimeManager.getInstance().getGameTime();
        final int h = t / 60;
        final int m = t % 60;
        final SimpleDateFormat format = new SimpleDateFormat("h:mm a");
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, h);
        cal.set(Calendar.MINUTE, m);

        final StringBuilder replyMSG = new StringBuilder("<html><body>");
        replyMSG.append("<table width=260><tr>");
        replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td width=180><center>Server Management Menu</center></td>");
        replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("</tr></table>");
        replyMSG.append("<br><br>");
        replyMSG.append("<table>");
        replyMSG.append("<tr><td>Players Online: ").append(GameObjectsStorage.getPlayers().size()).append("</td></tr>");
        replyMSG.append("<tr><td>Used Memory: ").append(StatsUtils.getMemUsedMb()).append("</td></tr>");
        replyMSG.append("<tr><td>Server Rates: ").append(ServerConfig.RATE_XP).append("x, ").append(ServerConfig.RATE_SP).append("x, ").append(
                ServerConfig.RATE_DROP_ADENA).append("x, ").append(ServerConfig.RATE_DROP_ITEMS).append("x</td></tr>");
        replyMSG.append("<tr><td>Game Time: ").append(format.format(cal.getTime())).append("</td></tr>");
        replyMSG.append("</table><br>");
        replyMSG.append("<table width=270>");
        replyMSG.append("<tr><td>Enter in seconds the time till the server shutdowns bellow:</td></tr>");
        replyMSG.append("<br>");
        replyMSG.append("<tr><td><center>Seconds till: <edit var=\"shutdown_time\" width=60></center></td></tr>");
        replyMSG.append("</table><br>");
        replyMSG.append("<center><table><tr><td>");
        replyMSG.append("<button value=\"Shutdown\" action=\"bypass -h admin_server_shutdown $shutdown_time\" width=60 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>");
        replyMSG.append("<button value=\"Restart\" action=\"bypass -h admin_server_restart $shutdown_time\" width=60 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>");
        replyMSG.append("<button value=\"Abort\" action=\"bypass -h admin_server_abort\" width=60 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
        replyMSG.append("</td></tr></table></center>");
        replyMSG.append("</body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_server_shutdown,
        admin_server_restart,
        admin_server_abort
    }
}