package services.villagemasters;

import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;

public class Clan
{
	@Bypass("services.villagemasters.Clan:CheckCreateClan")
	public void CheckCreateClan(Player pl, NpcInstance npc, String[] arg)
	{
		String htmltext = "clan-02.htm";
		// Player less 10 levels, and can not create clan
		if(pl.getLevel() <= 9)
		{
			htmltext = "clan-06.htm";
		}
		// Player already is a clan by leader and can not newly create clan
		else if(pl.isClanLeader())
		{
			htmltext = "clan-07.htm";
		}
		// Player already consists in clan and can not create clan
		else if(pl.getClan() != null)
		{
			htmltext = "clan-09.htm";
		}
		npc.showChatWindow(pl, "villagemaster/" + htmltext);
	}

	@Bypass("services.villagemasters.Clan:CheckDissolveClan")
	public void CheckDissolveClan(Player pl, NpcInstance npc, String[] arg)
	{
		String htmltext = "clan-01.htm";
		if(pl.isClanLeader())
		{
			htmltext = "clan-04.htm";
		}
		else
			// Player already consists in clan and can not create clan
			if(pl.getClan() != null)
			{
				htmltext = "9000-08.htm";
			}
			// Player not in clan and can not dismiss clan
			else
			{
				htmltext = "9000-11.htm";
			}
		npc.showChatWindow(pl, "villagemaster/" + htmltext);
	}
}