package handler.admincommands;

import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 2:52/29.06.2011
 */
public class AdminTeam extends ScriptAdminCommand
{
	enum Commands
	{
		admin_setteam
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		TeamType team = TeamType.NONE;
		if(wordList.length >= 2)
		{
			for(TeamType t : TeamType.values())
			{
				if(wordList[1].equalsIgnoreCase(t.name()))
				{
					team = t;
				}
			}
		}

		GameObject object = activeChar.getTarget();
		if(object == null || !object.isCreature())
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return false;
		}

		((Creature) object).setTeam(team);
		return true;
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
