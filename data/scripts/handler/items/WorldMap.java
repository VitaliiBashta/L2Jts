package handler.items;

import org.mmocore.gameserver.network.lineage.serverpackets.ShowMiniMap;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;

public class WorldMap extends ScriptItemHandler
{
	// all the items ids that this handler knowns
	private static final int[] _itemIds = { 1665, 1863, 9994 };

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
		{
			return false;
		}
		Player player = (Player) playable;

		player.sendPacket(new ShowMiniMap(player, item.getItemId()));
		return true;
	}

	@Override
	public final int[] getItemIds()
	{
		return _itemIds;
	}
}