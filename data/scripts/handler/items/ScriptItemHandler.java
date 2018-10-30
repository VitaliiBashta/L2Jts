package handler.items;

import org.mmocore.gameserver.handler.items.IItemHandler;
import org.mmocore.gameserver.handler.items.ItemHandler;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.Log;

/**
 * @author VISTALL
 * @date 21:09/12.07.2011
 */
public abstract class ScriptItemHandler implements OnInitScriptListener, IItemHandler
{
	@Override
	public void dropItem(Player player, ItemInstance item, long count, Location loc)
	{
		if(item.isEquipped())
		{
			player.getInventory().unEquipItem(item);
			player.sendUserInfo(true);
		}

		item = player.getInventory().removeItemByObjectId(item.getObjectId(), count);
		if(item == null)
		{
			player.sendActionFailed();
			return;
		}

		Log.items(player, Log.Drop, item);

		item.dropToTheGround(player, loc);
		player.disableDrop(1000);

		player.sendChanges();
	}

	@Override
	public boolean pickupItem(Playable playable, ItemInstance item)
	{
		return true;
	}

	@Override
	public void onInit()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}
}
