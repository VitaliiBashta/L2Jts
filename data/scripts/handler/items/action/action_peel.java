package handler.items.action;

import gnu.trove.set.hash.TIntHashSet;
import handler.items.ScriptItemHandler;

import java.util.List;

import org.jts.dataparser.data.holder.itemdata.ItemData.ItemAction;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.item.support.CapsuledItemsContainer;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author KilRoy
 */
public class action_peel extends ScriptItemHandler
{
	private final int[] itemIds;

	public action_peel()
	{
		final TIntHashSet set = new TIntHashSet();
		for(final ItemTemplate template : ItemTemplateHolder.getInstance().getAllTemplates())
		{
			if(template == null)
			{
				continue;
			}
			if(template.getActionType() == ItemAction.action_peel)
			{
				set.add(template.getItemId());
			}
		}
		itemIds = set.toArray();
	}

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;

		final Player player = (Player) playable;

		final boolean isCapsuled = item.getTemplate().isCapsuled();

		if (!canBeExtracted(player, item))
			return false;

		if (!useItem(player, item, 1))
			return false;

		final List<CapsuledItemsContainer> extracted_items = item.getTemplate().getCapsuledItems();
		boolean random = true;
		int sizer = 0;
		int maxSize = extracted_items.size();
		int preItemId = 0;
		int perMinCount = 0;
		int perMaxCount = 0;
		for(CapsuledItemsContainer extracted : extracted_items)
		{
			if(extracted.getChance() < 100.0)
			{
				if(isCapsuled && Rnd.chance(50) && preItemId == 0)
				{
					preItemId = extracted.getItemId();
					perMinCount = extracted.getMinCount();
					perMaxCount = extracted.getMaxCount();
				}
				if(Rnd.chance(extracted.getChance()) && random)
				{
					ItemFunctions.addItem(player, extracted.getItemId(), Rnd.get(extracted.getMinCount(), extracted.getMaxCount()));
					random = false;
				}
				if(isCapsuled)
				{
					sizer++;
					if(sizer == maxSize && random)
					{
						ItemFunctions.addItem(player, preItemId, Rnd.get(perMinCount, perMaxCount));
						random = false;
					}
				}
			}
			else if(extracted.getChance() == 100.0)
				ItemFunctions.addItem(player, extracted.getItemId(), Rnd.get(extracted.getMinCount(), extracted.getMaxCount()));
		}

		return true;
	}

	private static boolean canBeExtracted(Player player, ItemInstance item)
	{
		if(player.getWeightPenalty() >= 3 || player.getInventory().getSize() > player.getInventoryLimit() - 10)
		{
			player.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL, new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
			return false;
		}

		if(player.isActionsDisabled() || player.isInTrade() || player.isInStoreMode())
		{
			player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
			return false;
		}

		return true;
	}

	public static boolean useItem(Player player, ItemInstance item, long count)
	{
		if(player.getInventory().destroyItem(item, count))
		{
			player.sendPacket(new SystemMessage(SystemMsg.YOU_USE_S1).addItemName(item.getItemId()));
			return true;
		}

		player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
		return false;
	}

	@Override
	public final int[] getItemIds()
	{
		return itemIds;
	}
}