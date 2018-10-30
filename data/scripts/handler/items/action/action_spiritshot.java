package handler.items.action;

import gnu.trove.set.hash.TIntHashSet;
import handler.items.ScriptItemHandler;

import org.jts.dataparser.data.holder.itemdata.ItemData.ItemAction;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
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

/**
 * @author Unknown
 * @author KilRoy
 */
public class action_spiritshot extends ScriptItemHandler
{
	private static int[] itemIds;
	private static final int[] skillIdsSs = { 2061, 2155, 2156, 2157, 2158, 2159 };
	private static final int[] skillIdsBs = { 2061, 2160, 2161, 2162, 2163, 2164 };

	public action_spiritshot()
	{
		final TIntHashSet set = new TIntHashSet();
		for(final ItemTemplate template : ItemTemplateHolder.getInstance().getAllTemplates())
		{
			if(template == null)
			{
				continue;
			}
			if(template.getActionType() == ItemAction.action_spiritshot)
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
		final Player player = (Player) playable;
		final int spiritshotId = item.getItemId();
		boolean isAutoSoulShot = false;

		if(player.getAutoSoulShot().contains(spiritshotId))
		{
			isAutoSoulShot = true;
		}

		switch(spiritshotId)
		{
			case 2509:
			case 2510:
			case 2511:
			case 2512:
			case 2513:
			case 2514:
			case 5790:
			case 22077:
			case 22078:
			case 22079:
			case 22080:
			case 22081:
				if(!useSpiritShot(player, isAutoSoulShot, item))
					return false;
				break;
			case 3947:
			case 3948:
			case 3949:
			case 3950:
			case 3951:
			case 3952:
			case 22072:
			case 22073:
			case 22074:
			case 22075:
			case 22076:
				if(!useBlessedSpiritShot(player, isAutoSoulShot, item))
					return false;
				break;
		}
		return true;
	}

	private boolean useSpiritShot(final Player player, final boolean isAutoSoulShot, final ItemInstance item)
	{
		final int spiritshotId = item.getItemId();
		final ItemInstance weaponInst = player.getActiveWeaponInstance();
		final WeaponTemplate weaponItem = player.getActiveWeaponItem();
		if(weaponInst == null)
		{
			if(!isAutoSoulShot)
			{
				player.sendPacket(SystemMsg.YOU_MAY_NOT_USE_SPIRITSHOTS);
			}
			return false;
		}
		// spiritshot is already active
		if(weaponInst.getChargedSpiritshot() != ItemInstance.CHARGED_NONE)
		{
			return false;
		}

		int gradeSs = weaponItem.getCrystalType().externalOrdinal;
		int soulSpiritConsumption = weaponItem.getSpiritShotCount();
		long count = item.getCount();

		if(soulSpiritConsumption == 0)
		{
			// Can't use Spiritshots
			if(isAutoSoulShot)
			{
				player.removeAutoSoulShot(spiritshotId);
				player.sendPacket(new ExAutoSoulShot(spiritshotId, false), new SystemMessage(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED).addItemName(spiritshotId));
				return false;
			}
			player.sendPacket(SystemMsg.YOU_MAY_NOT_USE_SPIRITSHOTS);
			return false;
		}

		if(gradeSs == 0 && spiritshotId != 5790 && spiritshotId != 2509 // NG
		   || gradeSs == 1 && spiritshotId != 2510 && spiritshotId != 22077 // D
		   || gradeSs == 2 && spiritshotId != 2511 && spiritshotId != 22078 // C
		   || gradeSs == 3 && spiritshotId != 2512 && spiritshotId != 22079 // B
		   || gradeSs == 4 && spiritshotId != 2513 && spiritshotId != 22080 // A
		   || gradeSs == 5 && spiritshotId != 2514 && spiritshotId != 22081 // S
				)
		{
			// wrong grade for weapon
			if(isAutoSoulShot)
			{
				return false;
			}
			player.sendPacket(SystemMsg.YOUR_SPIRITSHOT_DOES_NOT_MATCH_THE_WEAPONS_GRADE);
			return false;
		}

		if(count < soulSpiritConsumption)
		{
			if(isAutoSoulShot)
			{
				player.removeAutoSoulShot(spiritshotId);
				player.sendPacket(new ExAutoSoulShot(spiritshotId, false), new SystemMessage(SystemMsg
						.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED).addItemName(spiritshotId));
				return false;
			}
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SPIRITSHOT_FOR_THAT);
			return false;
		}

		if (!AllSettingsConfig.unlimitedShots)
			if(player.getInventory().destroyItem(item, soulSpiritConsumption)) {
				player.checkAndRemoveInvisible();
				weaponInst.setChargedSpiritshot(ItemInstance.CHARGED_SPIRITSHOT);
				player.sendPacket(SystemMsg.YOUR_SPIRITSHOT_HAS_BEEN_ENABLED);
				player.broadcastPacket(new MagicSkillUse(player, player, skillIdsSs[gradeSs], 1, 0, 0));
			}
		return true;
	}

	private boolean useBlessedSpiritShot(final Player player, final boolean isAutoSoulShot, final ItemInstance item)
	{
		final int spiritshotId = item.getItemId();
		final ItemInstance weaponInst = player.getActiveWeaponInstance();
		final WeaponTemplate weaponItem = player.getActiveWeaponItem();
		// already charged by blessed spirit shotbtw we cant charge only when bsps is charged
		if(weaponInst == null)
		{
			if(!isAutoSoulShot)
			{
				player.sendPacket(SystemMsg.YOU_MAY_NOT_USE_SPIRITSHOTS);
			}
			return false;
		}
		if(weaponInst.getChargedSpiritshot() == ItemInstance.CHARGED_BLESSED_SPIRITSHOT)
		{
			return false;
		}
		int gradeBs = weaponItem.getCrystalType().externalOrdinal;
		int blessedsoulSpiritConsumption = weaponItem.getSpiritShotCount();

		if(blessedsoulSpiritConsumption == 0)
		{
			// Can't use Spiritshots
			if(isAutoSoulShot)
			{
				player.removeAutoSoulShot(spiritshotId);
				player.sendPacket(new ExAutoSoulShot(spiritshotId, false), new SystemMessage(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED).addItemName(spiritshotId));
				return false;
			}
			player.sendPacket(SystemMsg.YOU_MAY_NOT_USE_SPIRITSHOTS);
			return false;
		}

		if(gradeBs == 0 && spiritshotId != 3947 // NG
		   || gradeBs == 1 && spiritshotId != 3948 && spiritshotId != 22072 // D
		   || gradeBs == 2 && spiritshotId != 3949 && spiritshotId != 22073 // C
		   || gradeBs == 3 && spiritshotId != 3950 && spiritshotId != 22074 // B
		   || gradeBs == 4 && spiritshotId != 3951 && spiritshotId != 22075 // A
		   || gradeBs == 5 && spiritshotId != 3952 && spiritshotId != 22076 // S
				)
		{
			if(isAutoSoulShot)
			{
				return false;
			}
			player.sendPacket(SystemMsg.YOUR_SPIRITSHOT_DOES_NOT_MATCH_THE_WEAPONS_GRADE);
			return false;
		}

		if (!AllSettingsConfig.unlimitedShots)
			if(!player.getInventory().destroyItem(item, blessedsoulSpiritConsumption)) {
				if (isAutoSoulShot) {
					player.removeAutoSoulShot(spiritshotId);
					player.sendPacket(new ExAutoSoulShot(spiritshotId, false), new SystemMessage(SystemMsg.DUE_TO_INSUFFICIENT_S1_THE_AUTOMATIC_USE_FUNCTION_HAS_BEEN_DEACTIVATED).addItemName(spiritshotId));
					return false;
				}
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SPIRITSHOT_FOR_THAT);
				return false;
			}
		player.checkAndRemoveInvisible();
		weaponInst.setChargedSpiritshot(ItemInstance.CHARGED_BLESSED_SPIRITSHOT);
		player.sendPacket(SystemMsg.YOUR_SPIRITSHOT_HAS_BEEN_ENABLED);
		player.broadcastPacket(new MagicSkillUse(player, player, skillIdsBs[gradeBs], 1, 0, 0));
		return true;
	}

	@Override
	public final int[] getItemIds()
	{
		return itemIds;
	}
}