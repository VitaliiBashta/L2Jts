package handler.admincommands;

import org.mmocore.gameserver.network.lineage.serverpackets.SkillList;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntryType;

/**
 * @author VISTALL
 * @date 19:34/14.09.2011
 */
public class AdminSkillEntry extends ScriptAdminCommand
{
	enum Commands
	{
		admin_enable_by_entry,
		admin_disable_by_entry
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player player)
	{
		if(wordList.length != 2)
		{
			return false;
		}

		Player target = player;
		if(player.getTarget() != null && player.getTarget().isPlayer())
		{
			target = player.getTarget().getPlayer();
		}

		SkillEntryType type = null;
		try
		{
			type = SkillEntryType.valueOf(wordList[1].toUpperCase());
		}
		catch(IllegalArgumentException e)
		{
			player.sendAdminMessage("Unknown skill entry type " + wordList[1]);
		}
		Commands c = (Commands) comm;
		switch(c)
		{
			case admin_enable_by_entry:
				target.enableSkillsByEntryType(type);
				break;
			case admin_disable_by_entry:
				target.disableSkillsByEntryType(type);
				break;
		}
		target.sendPacket(new SkillList(target));
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
