package handler.items;

import org.mmocore.gameserver.model.items.etcitems.EnchantScrolls.EnchantScrollManager;
import org.mmocore.gameserver.network.lineage.serverpackets.ChooseInventoryItem;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;

/**
 * @author ALF, KilRoy
 * @date 29.08.2012
 */
public class EnchantScrolls extends ScriptItemHandler
{
	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;
		Player player = (Player) playable;

		if(player.getEnchantScroll() != null)
			return false;

		player.setEnchantScroll(item);
		player.sendPacket(new ChooseInventoryItem(item.getItemId()));
		return true;
	}

	@Override
	public final int[] getItemIds()
	{
		return EnchantScrollManager.getEnchantScrollIds();
	}
}