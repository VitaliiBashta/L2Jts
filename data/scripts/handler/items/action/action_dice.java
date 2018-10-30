package handler.items.action;

import gnu.trove.set.hash.TIntHashSet;
import handler.items.ScriptItemHandler;

import org.jts.dataparser.data.holder.itemdata.ItemData.ItemAction;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.Dice;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.item.ItemTemplate;

/**
 * @author Unknown
 * @author KilRoy
 */
public class action_dice extends ScriptItemHandler
{
	private static int[] itemIds;

	public action_dice()
	{
		final TIntHashSet set = new TIntHashSet();
		for(final ItemTemplate template : ItemTemplateHolder.getInstance().getAllTemplates())
		{
			if(template == null)
			{
				continue;
			}
			if(template.getActionType() == ItemAction.action_dice)
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
		{
			return false;
		}
		Player player = (Player) playable;

		int itemId = item.getItemId();

		if(player.isInOlympiadMode())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_USE_THAT_ITEM_IN_A_GRAND_OLYMPIAD_MATCH);
			return false;
		}

		if(player.isSitting())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_SITTING);
			return false;
		}

		int number = Rnd.get(1, 6);
		if(number == 0)
		{
			player.sendPacket(SystemMsg.YOU_MAY_NOT_THROW_THE_DICE_AT_THIS_TIME_TRY_AGAIN_LATER);
			return false;
		}

		player.broadcastPacket(new Dice(player.getObjectId(), itemId, number, player.getX() - 30, player.getY() - 30, player.getZ()), new SystemMessage(SystemMsg.S1_HAS_ROLLED_S2).addString(player.getName()).addNumber(number));

		return true;
	}

	@Override
	public final int[] getItemIds()
	{
		return itemIds;
	}
}