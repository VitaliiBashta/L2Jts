package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author pchayka
 */

public final class SairlenGatekeeperInstance extends NpcInstance {
    private static final int GAZKH = 8784;

    public SairlenGatekeeperInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }
/* TODO: Доконца протестить и удалить
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
		{
			return;
		}

		if(command.startsWith("request_entrance"))
		{
			if(player.getLevel() < 75)
			{
				showChatWindow(player, "default/32109-3.htm");
			}
			else if(ItemFunctions.getItemCount(player, GAZKH) > 0)
			{
				int check = SailrenManager.canIntoSailrenLair(player);
				if(check == 1 || check == 2)
				{
					showChatWindow(player, "default/32109-5.htm");
				}
				else if(check == 3)
				{
					showChatWindow(player, "default/32109-4.htm");
				}
				else if(check == 4)
				{
					showChatWindow(player, "default/32109-1.htm");
				}
				else if(check == 0)
				{
					ItemFunctions.removeItem(player, GAZKH, 1, true);
					SailrenManager.setSailrenSpawnTask();
					SailrenManager.entryToSailrenLair(player);
				}
			}
			else
			{
				showChatWindow(player, "default/32109-2.htm");
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
	*/
}