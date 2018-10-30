package handler.items.action;

import gnu.trove.set.hash.TIntHashSet;
import handler.items.ScriptItemHandler;

import org.jts.dataparser.data.holder.itemdata.ItemData.ItemAction;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExAutoSoulShot;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.item.WeaponTemplate;
import org.mmocore.gameserver.templates.item.WeaponTemplate.WeaponType;

/**
 * @author Unknown
 * @author KilRoy
 */
public class action_fishingshot extends ScriptItemHandler
{
	private static int[] itemIds;
	private static int[] skillIdsFs = { 2181, 2182, 2183, 2184, 2185, 2186 };

	public action_fishingshot()
	{
		final TIntHashSet set = new TIntHashSet();
		for(final ItemTemplate template : ItemTemplateHolder.getInstance().getAllTemplates())
		{
			if(template == null)
			{
				continue;
			}
			if(template.getActionType() == ItemAction.action_fishingshot)
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
		int FishshotId = item.getItemId();

		boolean isAutoSoulShot = false;
		if(player.getAutoSoulShot().contains(FishshotId))
		{
			isAutoSoulShot = true;
		}

		ItemInstance weaponInst = player.getActiveWeaponInstance();
		WeaponTemplate weaponItem = player.getActiveWeaponItem();

		if(weaponInst == null || weaponItem.getItemType() != WeaponType.ROD)
		{
			if(!isAutoSoulShot)
			{
				player.sendPacket(SystemMsg.CANNOT_USE_SOULSHOTS);
			}
			return false;
		}

		// spiritshot is already active
		if(weaponInst.getChargedFishshot())
		{
			return false;
		}

		if(item.getCount() < 1)
		{
			if(isAutoSoulShot)
			{
				player.removeAutoSoulShot(FishshotId);
				player.sendPacket(new ExAutoSoulShot(FishshotId, false), new SystemMessage(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED).addString(item.getName()));
				return false;
			}
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SPIRITSHOT_FOR_THAT);
			return false;
		}

		int grade = weaponItem.getCrystalType().externalOrdinal;

		if(grade == 0 && FishshotId != 6535 || grade == 1 && FishshotId != 6536 || grade == 2 && FishshotId != 6537 || grade == 3 && FishshotId != 6538 || grade == 4 && FishshotId != 6539 || grade == 5 && FishshotId != 6540)
		{
			if(isAutoSoulShot)
			{
				return false;
			}
			player.sendPacket(SystemMsg.THAT_IS_THE_WRONG_GRADE_OF_SOULSHOT_FOR_THAT_FISHING_POLE);
			return false;
		}

		if(player.getInventory().destroyItem(item, 1L))
		{
			weaponInst.setChargedFishshot(true);
			player.sendPacket(SystemMsg.YOUR_SPIRITSHOT_HAS_BEEN_ENABLED);
			player.broadcastPacket(new MagicSkillUse(player, player, skillIdsFs[grade], 1, 0, 0));
		}
		return true;
	}

	@Override
	public int[] getItemIds()
	{
		return itemIds;
	}
}