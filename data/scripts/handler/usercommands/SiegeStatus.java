package handler.usercommands;

import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.entity.events.objects.SiegeClanObject;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.pledge.UnitMember;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @author Java-man
 * @date 21:54/08.03.2011
 */
public class SiegeStatus extends ScriptUserCommand
{
	public static final int[] COMMANDS = { 99 };

	private static final String INSIDE_SIEGE_ZONE = "Castle Siege in Progress";
	private static final String OUTSIDE_SIEGE_ZONE = "No Castle Siege Area";

	@Override
	public boolean useUserCommand(final int id, final Player player)
	{
		if(!player.isClanLeader())
		{
			player.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_MAY_ISSUE_COMMANDS);
			return false;
		}

		final Castle castle = player.getCastle();
		if(castle == null)
			return false;

		final SiegeEvent<?, ?> siegeEvent = castle.getSiegeEvent();

		if(siegeEvent.isInProgress())
		{
			if(!player.isNoble())
			{
				player.sendPacket(SystemMsg.ONLY_A_CLAN_LEADER_THAT_IS_A_NOBLESSE_CAN_VIEW_THE_SIEGE_WAR_STATUS_WINDOW_DURING_A_SIEGE_WAR);
				return false;
			}
		}
		else
		{
			player.sendPacket(SystemMsg.YOU_CAN_ONLY_USE_THAT_DURING_A_SIEGE_WAR);
			return false;
		}

		final SiegeClanObject clan = siegeEvent.getSiegeClan(siegeEvent.getClass() == DominionSiegeEvent.class
		                                                     ? SiegeEvent.DEFENDERS : SiegeEvent.ATTACKERS, player.getClan());

		final StringBuilder memberList = new StringBuilder();
		for(final UnitMember member : clan.getClan())
		{
			memberList.append("<tr><td width=170>");
			memberList.append(member.getName());
			memberList.append("</td><td width=100>");
			memberList.append(siegeEvent.checkIfInZone(member.getPlayer()) ? INSIDE_SIEGE_ZONE : OUTSIDE_SIEGE_ZONE);
			memberList.append("</td></tr>");
		}

		final HtmlMessage msg = new HtmlMessage(5);
		msg.setFile("command/siege_status.htm");
		msg.replace("%member_list%", memberList.toString());

		player.sendPacket(msg);
		return true;
	}

	@Override
	public int[] getUserCommandList()
	{
		return COMMANDS;
	}
}
