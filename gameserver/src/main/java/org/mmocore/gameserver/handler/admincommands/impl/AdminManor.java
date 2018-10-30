package org.mmocore.gameserver.handler.admincommands.impl;


import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.manager.CastleManorManager;
import org.mmocore.gameserver.manager.ServerVariables;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.manor.CropProcure;
import org.mmocore.gameserver.templates.manor.SeedProduction;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Admin comand handler for Manor System
 * This class handles following admin commands:
 * - manor_info = shows info about current manor state
 * - manor_approve = approves settings for the next manor period
 * - manor_setnext = changes manor settings to the next day's
 * - manor_reset castle = resets all manor data for specified castle (or all)
 * - manor_setmaintenance = sets manor system under maintenance mode
 * - manor_save = saves all manor data into database
 * - manor_disable = disables manor system
 */
public class AdminManor implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, String fullString, final Player activeChar) {
        if (!activeChar.getPlayerAccess().Menu) {
            return false;
        }

        final StringTokenizer st = new StringTokenizer(fullString);
        fullString = st.nextToken();

        switch (fullString) {
            case "admin_manor":
                showMainPage(activeChar);
                break;
            case "admin_manor_reset":
                int castleId = 0;
                try {
                    castleId = Integer.parseInt(st.nextToken());
                } catch (Exception e) {
                }

                if (castleId > 0) {
                    final Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, castleId);
                    castle.setCropProcure(new ArrayList<CropProcure>(), CastleManorManager.PERIOD_CURRENT);
                    castle.setCropProcure(new ArrayList<CropProcure>(), CastleManorManager.PERIOD_NEXT);
                    castle.setSeedProduction(new ArrayList<SeedProduction>(), CastleManorManager.PERIOD_CURRENT);
                    castle.setSeedProduction(new ArrayList<SeedProduction>(), CastleManorManager.PERIOD_NEXT);
                    castle.saveCropData();
                    castle.saveSeedData();
                    activeChar.sendAdminMessage("Manor data for " + castle.getName() + " was nulled");
                } else {
                    for (final Castle castle : ResidenceHolder.getInstance().getResidenceList(Castle.class)) {
                        castle.setCropProcure(new ArrayList<CropProcure>(), CastleManorManager.PERIOD_CURRENT);
                        castle.setCropProcure(new ArrayList<CropProcure>(), CastleManorManager.PERIOD_NEXT);
                        castle.setSeedProduction(new ArrayList<SeedProduction>(), CastleManorManager.PERIOD_CURRENT);
                        castle.setSeedProduction(new ArrayList<SeedProduction>(), CastleManorManager.PERIOD_NEXT);
                        castle.saveCropData();
                        castle.saveSeedData();
                    }
                    activeChar.sendAdminMessage("Manor data was nulled");
                }
                showMainPage(activeChar);
                break;
            case "admin_manor_save":
                CastleManorManager.getInstance().save();
                activeChar.sendAdminMessage("Manor System: all data saved");
                showMainPage(activeChar);
                break;
            case "admin_manor_disable":
                final boolean mode = CastleManorManager.getInstance().isDisabled();
                CastleManorManager.getInstance().setDisabled(!mode);
                if (mode) {
                    activeChar.sendAdminMessage("Manor System: enabled");
                } else {
                    activeChar.sendAdminMessage("Manor System: disabled");
                }
                showMainPage(activeChar);
                break;
        }

        return true;
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    private void showMainPage(final Player activeChar) {
        final HtmlMessage adminReply = new HtmlMessage(5);
        final StringBuilder replyMSG = new StringBuilder("<html><body>");

        replyMSG.append("<center><font color=\"LEVEL\"> [Manor System] </font></center><br>");
        replyMSG.append("<table width=\"100%\">");
        replyMSG.append("<tr><td>Disabled: ").append(CastleManorManager.getInstance().isDisabled() ? "yes" : "no").append("</td>");
        replyMSG.append("<td>Under Maintenance: ").append(CastleManorManager.getInstance().isUnderMaintenance() ? "yes" : "no").append("</td></tr>");
        replyMSG.append("<tr><td>Approved: ").append(ServerVariables.getBool("ManorApproved") ? "yes" : "no").append("</td></tr>");
        replyMSG.append("</table>");

        replyMSG.append("<center><table>");
        replyMSG.append("<tr><td><button value=\"").append(CastleManorManager.getInstance().isDisabled() ? "Enable" : "Disable").append(
                "\" action=\"bypass -h admin_manor_disable\" width=110 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td><button value=\"Reset\" action=\"bypass -h admin_manor_reset\" width=110 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
        replyMSG.append("<tr><td><button value=\"Refresh\" action=\"bypass -h admin_manor\" width=110 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td><button value=\"Back\" action=\"bypass -h admin_admin\" width=110 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
        replyMSG.append("</table></center>");

        replyMSG.append("<br><center>Castle Information:<table width=\"100%\">");
        replyMSG.append("<tr><td></td><td>Current Period</td><td>Next Period</td></tr>");

        for (final Castle c : ResidenceHolder.getInstance().getResidenceList(Castle.class)) {
            replyMSG.append("<tr><td>").append(c.getName()).append("</td>").append("<td>").append(c.getManorCost(CastleManorManager.PERIOD_CURRENT))
                    .append("a</td>").append("<td>").append(c.getManorCost(CastleManorManager.PERIOD_NEXT)).append("a</td>").append("</tr>");
        }
        replyMSG.append("</table><br>");

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
        admin_manor,
        admin_manor_reset,
        admin_manor_save,
        admin_manor_disable
    }
}