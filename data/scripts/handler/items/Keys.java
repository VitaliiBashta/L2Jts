package handler.items;

import org.mmocore.gameserver.data.xml.holder.DoorHolder;
import org.mmocore.gameserver.model.instances.DoorInstance;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.DoorTemplate;

import gnu.trove.set.hash.TIntHashSet;


public class Keys extends ScriptItemHandler
{
	private int[] _itemIds = null;

	public Keys()
	{
		TIntHashSet keys = new TIntHashSet();
		for(DoorTemplate door : DoorHolder.getInstance().getDoors().valueCollection())
		{
			if(door != null && door.getKey() > 0)
			{
				keys.add(door.getKey());
			}
		}
		_itemIds = keys.toArray();
	}

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
		{
			return false;
		}
		Player player = playable.getPlayer();
		GameObject target = player.getTarget();
		if(target == null || !target.isDoor())
		{
			player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}
		DoorInstance door = (DoorInstance) target;
		if(door.isOpen())
		{
			player.sendPacket(SystemMsg.IT_IS_NOT_LOCKED);
			return false;
		}
		if(door.getKey() <= 0 || item.getItemId() != door.getKey()) // ключ не подходит к двери
		{
			player.sendPacket(SystemMsg.YOU_ARE_UNABLE_TO_UNLOCK_THE_DOOR);
			return false;
		}
		if(player.getDistance(door) > 300)
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_CONTROL_BECAUSE_YOU_ARE_TOO_FAR);
			return false;
		}
		if(!player.getInventory().destroyItem(item, 1L))
		{
			player.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
			return false;
		}
		player.sendPacket(SystemMessage.removeItems(item.getItemId(), 1));
		player.sendMessage(new CustomMessage("org.mmocore.gameserver.skills.skillclasses.Unlock.Success"));
		door.openMe(player, true);
		return true;
	}

	@Override
	public int[] getItemIds()
	{
		return _itemIds;
	}
}
