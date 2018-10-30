package handler.bbs.custom;

import handler.bbs.ScriptBbsHandler;
import org.mmocore.commons.converter.Converter;
import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.events.custom.CaptureTeamFlagEvent;
import org.mmocore.gameserver.scripts.events.custom.TeamVsTeamEvent;

/**
 * @author VISTALL
 * @date 15:16/06.09.2011
 */
public class CommunityEvents extends ScriptBbsHandler
{
	@Override
	public String[] getBypassCommands()
	{
		return new String[] { "_bbsevent" };
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		final String[] mBypass = bypass.split(":");
		final String eventName = mBypass[1];
		switch(eventName)
		{
			case "tvt":
			{
				final int id = Converter.convert(Integer.class, mBypass[2]);
				final TeamVsTeamEvent event = EventHolder.getInstance().getEvent(EventType.PVP_EVENT, id);
				if(event == null)
				{
					return;
				}
				player.sendPacket(new HtmlMessage(0).setFile(event.registerPlayer(player) ? "events/tvt_reg_succ.htm" : "events/tvt_reg_fail.htm"));
				break;
			}
			case "ctf":
			{
				final int id = Converter.convert(Integer.class, mBypass[2]);
				final CaptureTeamFlagEvent event = EventHolder.getInstance().getEvent(EventType.PVP_EVENT, id);
				if(event == null)
				{
					return;
				}

				player.sendPacket(new HtmlMessage(0).setFile(event.registerPlayer(player) ? "events/ctf_reg_succ.htm" : "events/ctf_reg_fail.htm"));
				break;
			}
			case "unreg":
			{
				final int id = Converter.convert(Integer.class, mBypass[2]);
				if(EventHolder.getInstance().getEvent(EventType.PVP_EVENT, id) == null)
				{
					return;
				}
				player.sendPacket(new HtmlMessage(0).setFile(EventHolder.getInstance().getEvent(EventType.PVP_EVENT, id).unregisterPlayer(player) ? "events/unreg_succ.htm" : "events/unreg_fail.htm"));
				break;
			}
		}
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		//
	}
}
