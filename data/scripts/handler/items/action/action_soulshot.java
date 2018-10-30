package handler.items.action;

import gnu.trove.set.hash.TIntHashSet;
import handler.items.ScriptItemHandler;

import org.jts.dataparser.data.holder.itemdata.ItemData.ItemAction;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExAutoSoulShot;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.item.WeaponTemplate;
import org.mmocore.gameserver.templates.item.WeaponTemplate.WeaponType;

/**
 * @author Unknown
 * @author KilRoy
 */
public class action_soulshot extends ScriptItemHandler
{
	private static int[] itemIds;
	private static final int[] _skillIds = { 2039, 2150, 2151, 2152, 2153, 2154 };

	public action_soulshot()
	{
		final TIntHashSet set = new TIntHashSet();
		for(final ItemTemplate template : ItemTemplateHolder.getInstance().getAllTemplates())
		{
			if(template == null)
			{
				continue;
			}
			if(template.getActionType() == ItemAction.action_soulshot)
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

		WeaponTemplate weaponItem = player.getActiveWeaponItem();

		ItemInstance weaponInst = player.getActiveWeaponInstance();
		int SoulshotId = item.getItemId();
		boolean isAutoSoulShot = false;

		if(player.getAutoSoulShot().contains(SoulshotId))
		{
			isAutoSoulShot = true;
		}

		if(weaponInst == null)
		{
			if(!isAutoSoulShot)
			{
				player.sendPacket(SystemMsg.CANNOT_USE_SOULSHOTS);
			}
			return false;
		}

		// soulshot is already active
		if(weaponInst.getChargedSoulshot() != ItemInstance.CHARGED_NONE)
		{
			return false;
		}

		int grade = weaponItem.getCrystalType().externalOrdinal;
		int soulShotConsumption = weaponItem.getSoulShotCount();

		if(soulShotConsumption == 0)
		{
			// Can't use soulshots
			if(isAutoSoulShot)
			{
				player.removeAutoSoulShot(SoulshotId);
				player.sendPacket(new ExAutoSoulShot(SoulshotId, false), new SystemMessage(SystemMsg
						.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED).addItemName(SoulshotId));
				return false;
			}
			player.sendPacket(SystemMsg.CANNOT_USE_SOULSHOTS);
			return false;
		}

		if(grade == 0 && SoulshotId != 5789 && SoulshotId != 1835 // NG
		   || grade == 1 && SoulshotId != 1463 && SoulshotId != 22082 && SoulshotId != 13037 // D
		   || grade == 2 && SoulshotId != 1464 && SoulshotId != 22083 && SoulshotId != 13045 // C
		   || grade == 3 && SoulshotId != 1465 && SoulshotId != 22084 // B
		   || grade == 4 && SoulshotId != 1466 && SoulshotId != 22085 && SoulshotId != 13055 // A
		   || grade == 5 && SoulshotId != 1467 && SoulshotId != 22086 // S
				)
		{
			// wrong grade for weapon
			if(isAutoSoulShot)
			{
				return false;
			}
			player.sendPacket(SystemMsg.THE_SOULSHOT_YOU_ARE_ATTEMPTING_TO_USE_DOES_NOT_MATCH_THE_GRADE_OF_YOUR_EQUIPPED_WEAPON);
			return false;
		}

		if(weaponItem.getItemType() == WeaponType.BOW || weaponItem.getItemType() == WeaponType.CROSSBOW)
		{
			int newSS = (int) player.calcStat(Stats.SS_USE_BOW, soulShotConsumption, null, null);
			if(newSS < soulShotConsumption && Rnd.chance(player.calcStat(Stats.SS_USE_BOW_CHANCE, soulShotConsumption, null, null)))
			{
				soulShotConsumption = newSS;
			}
		}

		if (!AllSettingsConfig.unlimitedShots)
			if (!player.getInventory().destroyItem(item, soulShotConsumption)) {
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SOULSHOTS_FOR_THAT);
				return false;
			}
		player.checkAndRemoveInvisible();
		weaponInst.setChargedSoulshot(ItemInstance.CHARGED_SOULSHOT);
		player.sendPacket(SystemMsg.YOUR_SOULSHOTS_ARE_ENABLED);
		player.broadcastPacket(new MagicSkillUse(player, player, _skillIds[grade], 1, 0, 0));
		return true;
	}

	@Override
	public final int[] getItemIds()
	{
		return itemIds;
	}
}