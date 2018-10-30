package handler.items;

import org.jts.dataparser.data.holder.PetDataHolder;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.tables.SkillTable;

public class PetSummon extends ScriptItemHandler
{
	// all the items ids that this handler knowns
	private static final int[] _itemIds = PetDataHolder.getInstance().getPetControlItems();
	private static final int _skillId = 2046;

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
		{
			return false;
		}
		Player player = (Player) playable;

		player.setPetControlItem(item);
		player.getAI().Cast(SkillTable.getInstance().getSkillEntry(_skillId, 1), player, false, true);
		return true;
	}

	@Override
	public final int[] getItemIds()
	{
		return _itemIds;
	}
}