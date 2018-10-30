package handler.items;

import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;

public abstract class SimpleItemHandler extends ScriptItemHandler
{
	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		final Player player;
		if(playable.isPlayer())
		{
			player = (Player) playable;
		}
		else if(playable.isPet())
		{
			player = playable.getPlayer();
		}
		else
		{
			return false;
		}

		if(player.isInFlyingTransform())
		{
			player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
			return false;
		}

		return useItemImpl(player, item, ctrl);
	}

	protected abstract boolean useItemImpl(Player player, ItemInstance item, boolean ctrl);

	public static boolean useItem(Player player, ItemInstance item, long count)
	{
		if(player.getInventory().destroyItem(item, count))
		{
			player.sendPacket(new SystemMessage(SystemMsg.YOU_USE_S1).addItemName(item.getItemId()));
			return true;
		}

		player.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
		return false;
	}
}
