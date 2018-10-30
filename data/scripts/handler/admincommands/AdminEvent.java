package handler.admincommands;

import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 18:45/07.06.2011
 */
public class AdminEvent extends ScriptAdminCommand
{
	enum Commands
	{
		admin_list_events
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands c = (Commands) comm;
		switch(c)
		{
			case admin_list_events:
				GameObject object = activeChar.getTarget();
				if(object == null)
				{
					activeChar.sendPacket(SystemMsg.INVALID_TARGET);
				}
				else
				{
					for(Event e : object.getEvents())
					{
						activeChar.sendAdminMessage("- " + e.toString());
					}
				}
				break;
		}
		return false;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	@Override
	public String[] getAdminCommandString()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
