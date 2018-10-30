package handler.items;

import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExGetBookMarkInfo;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.tp_bookmark.TeleportBookMarkComponent;

/**
 * @author VISTALL
 * @date 15:19/08.08.2011
 */
public class MyTeleportSpellbook extends ScriptItemHandler
{
	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(!playable.isPlayer())
		{
			return false;
		}

		Player player = (Player) playable;
		if(player.getTeleportBookMarkComponent().getTpBookmarkSize() >= TeleportBookMarkComponent.MAX_TELEPORT_BOOKMARK_SIZE)
		{
			player.sendPacket(SystemMsg.YOUR_NUMBER_OF_MY_TELEPORTS_SLOTS_HAS_REACHED_ITS_MAXIMUM_LIMIT);
			return false;
		}

		if(playable.consumeItem(item.getItemId(), 1))
		{
			player.getTeleportBookMarkComponent().setTpBookmarkSize(player.getTeleportBookMarkComponent().getTpBookmarkSize() + 3);
			player.sendPacket(SystemMsg.THE_NUMBER_OF_MY_TELEPORTS_SLOTS_HAS_BEEN_INCREASED);

			player.sendPacket(new ExGetBookMarkInfo(player));
		}
		return true;
	}

	@Override
	public int[] getItemIds()
	{
		return new int[] { 13015 };
	}
}
