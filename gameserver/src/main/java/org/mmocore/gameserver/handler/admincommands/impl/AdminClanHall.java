package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.model.entity.residence.ClanHall;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.ClanTable;

public class AdminClanHall implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanEditNPC) {
            return false;
        }

        ClanHall clanhall = null;
        if (wordList.length > 1) {
            clanhall = ResidenceHolder.getInstance().getResidence(ClanHall.class, Integer.parseInt(wordList[1]));
        }

        if (clanhall == null) {
            showClanHallSelectPage(activeChar);
            return true;
        }

        switch (command) {
            case admin_clanhall:
                showClanHallSelectPage(activeChar);
                break;
            case admin_clanhallset:
                final GameObject target = activeChar.getTarget();
                Player player = activeChar;
                if (target != null && target.isPlayer()) {
                    player = (Player) target;
                }
                if (player.getClan() == null) {
                    activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
                } else {
                    clanhall.changeOwner(player.getClan());
                }
                break;
            case admin_clanhalldel:
                clanhall.changeOwner(null);
                break;
            case admin_clanhallteleportself:
                final Zone zone = clanhall.getZone();
                if (zone != null) {
                    activeChar.teleToLocation(zone.getSpawn());
                }
                break;
        }
        showClanHallPage(activeChar, clanhall);
        return true;
    }

    public void showClanHallSelectPage(final Player activeChar) {
        final HtmlMessage adminReply = new HtmlMessage(5);

        final StringBuilder replyMSG = new StringBuilder("<html><body>");
        replyMSG.append("<table width=268><tr>");
        replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td width=180><center><font color=\"LEVEL\">Clan Halls:</font></center></td>");
        replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("</tr></table><br>");

        replyMSG.append("<table width=268>");
        replyMSG.append("<tr><td width=130>ClanHall Name</td><td width=58>Town</td><td width=80>Owner</td></tr>");

        // TODO: make sort by Location
		/*for(ClanHall clanhall : ClanHallManager.getInstance().getClanHalls().values())
			if(clanhall != null)
			{
				replyMSG.append("<tr><td>");
				replyMSG.append("<a action=\"bypass -h admin_clanhall " + clanhall.getId() + "\">" + clanhall.getName() + "</a>");
				replyMSG.append("</td><td>" + clanhall.getLocation() + "</td><td>");

				L2Clan owner = clanhall.getOwnerId() == 0 ? null : ClanTable.getInstance().getClan(clanhall.getOwnerId());
				if(owner == null)
					replyMSG.append("none");
				else
					replyMSG.append(owner.getName());

				replyMSG.append("</td></tr>");
			} */

        replyMSG.append("</table>");
        replyMSG.append("</body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);
    }

    public void showClanHallPage(final Player activeChar, final ClanHall clanhall) {
        final HtmlMessage adminReply = new HtmlMessage(5);
        final StringBuilder replyMSG = new StringBuilder("<html><body>");
        replyMSG.append("<table width=260><tr>");
        replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td width=180><center>ClanHall Name</center></td>");
        replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_clanhall\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("</tr></table>");
        replyMSG.append("<center>");
        replyMSG.append("<br><br><br>ClanHall: ").append(clanhall.getName()).append("<br>");
        replyMSG.append("Location: &^").append(clanhall.getId()).append(";<br>");
        replyMSG.append("ClanHall Owner: ");
        final Clan owner = clanhall.getOwnerId() == 0 ? null : ClanTable.getInstance().getClan(clanhall.getOwnerId());
        if (owner == null) {
            replyMSG.append("none");
        } else {
            replyMSG.append(owner.getName());
        }

        replyMSG.append("<br><br><br>");
        replyMSG.append("<table>");
        replyMSG.append("<tr><td><button value=\"Open Doors\" action=\"bypass -h admin_clanhallopendoors ").append(clanhall.getId()).append(
                "\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td><button value=\"Close Doors\" action=\"bypass -h admin_clanhallclosedoors ").append(clanhall.getId()).append(
                "\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
        replyMSG.append("</table>");
        replyMSG.append("<br>");
        replyMSG.append("<table>");
        replyMSG.append("<tr><td><button value=\"Give ClanHall\" action=\"bypass -h admin_clanhallset ").append(clanhall.getId()).append(
                "\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td><button value=\"Take ClanHall\" action=\"bypass -h admin_clanhalldel ").append(clanhall.getId()).append(
                "\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
        replyMSG.append("</table>");
        replyMSG.append("<br>");
        replyMSG.append("<table><tr>");
        replyMSG.append("<td><button value=\"Teleport self\" action=\"bypass -h admin_clanhallteleportself ").append(clanhall.getId()).append(
                " \" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
        replyMSG.append("</table>");
        replyMSG.append("</center>");
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
        admin_clanhall,
        admin_clanhallset,
        admin_clanhalldel,
        admin_clanhallteleportself
    }
}