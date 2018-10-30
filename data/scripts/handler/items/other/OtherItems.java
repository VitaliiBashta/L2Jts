package handler.items.other;

import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.tables.SkillTable;

import handler.items.SimpleItemHandler;

//TODO: придумать другой способ.....
public class OtherItems extends SimpleItemHandler
{
	private static final int[] ITEM_IDS = new int[] { 9207, 21040, 20582, 20545, 20546,21039 };

	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}

	@Override
	protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl)
	{
		int itemId = item.getItemId();
		if(player.isInOlympiadMode())
		{
			player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
			return false;
		}
		if(!useItem(player, item, 1))
		{
			return false;
		}
		switch(itemId)
		{
			//Event EvasInferno или как то так....
			// Quick Healing Potion (Event)
			case 9207:
				player.broadcastPacket(new MagicSkillUse(player, player, 2038, 1, 0, 0));
				player.altOnMagicUseTimer(player, SkillTable.getInstance().getSkillEntry(2038, 1));
				break;
			// Highest Power Temporary Healing Potion
			case 21040:
				player.broadcastPacket(new MagicSkillUse(player, player, 2864, 1, 0, 0));
				player.altOnMagicUseTimer(player, SkillTable.getInstance().getSkillEntry(2864, 1));
				break;
			// Power Juice
			case 20582:
				player.broadcastPacket(new MagicSkillUse(player, player, 2436, 1, 0, 0));
				player.altOnMagicUseTimer(player, SkillTable.getInstance().getSkillEntry(2436, 1));
				break;
			// Haste Juice
			case 20545:
				player.broadcastPacket(new MagicSkillUse(player, player, 2429, 1, 0, 0));
				player.altOnMagicUseTimer(player, SkillTable.getInstance().getSkillEntry(2429, 1));
				break;
			// Critical Attack Juice
			case 20546:
				player.broadcastPacket(new MagicSkillUse(player, player, 2432, 1, 0, 0));
				player.altOnMagicUseTimer(player, SkillTable.getInstance().getSkillEntry(2432, 1));
				break;
				
			//Highest Power Healing Potion
			case 21039:
				player.broadcastPacket(new MagicSkillUse(player, player, 2863, 1, 0, 0));
				player.altOnMagicUseTimer(player, SkillTable.getInstance().getSkillEntry(2863, 1));
				break;	
			default:
				return false;
		}
		return true;
	}
}
