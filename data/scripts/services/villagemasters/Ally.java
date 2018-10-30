package services.villagemasters;

import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;

public class Ally
{
	@Bypass("services.villagemasters.Ally:CheckCreateAlly")
	public void CheckCreateAlly(Player pl, NpcInstance npc, String[] arg)
	{
		String htmltext = "ally-01.htm";
		if(pl.isClanLeader())
		{
			htmltext = "ally-02.htm";
		}
		npc.showChatWindow(pl, "villagemaster/" + htmltext);
	}

	@Bypass("services.villagemasters.Ally:CheckDissolveAlly")
	public void CheckDissolveAlly(Player pl, NpcInstance npc, String[] arg)
	{
		String htmltext = "ally-01.htm";
		if(pl.isAllyLeader())
		{
			htmltext = "ally-03.htm";
		}
		npc.showChatWindow(pl, "villagemaster/" + htmltext);
	}
}