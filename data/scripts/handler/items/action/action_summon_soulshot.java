package handler.items.action;

import gnu.trove.set.hash.TIntHashSet;
import handler.items.ScriptItemHandler;

import org.jts.dataparser.data.holder.itemdata.ItemData.ItemAction;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.item.ItemTemplate;

/**
 * @author Unknown
 * @author KilRoy
 */
public class action_summon_soulshot extends ScriptItemHandler
{
	private static int[] itemIds;

	public action_summon_soulshot()
	{
		final TIntHashSet set = new TIntHashSet();
		for(final ItemTemplate template : ItemTemplateHolder.getInstance().getAllTemplates())
		{
			if(template == null)
			{
				continue;
			}
			if(template.getActionType() == ItemAction.action_summon_soulshot)
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

		boolean isAutoSoulShot = false;
		if(player.getAutoSoulShot().contains(item.getItemId()))
		{
			isAutoSoulShot = true;
		}

		Servitor pet = player.getServitor();
		if(pet == null)
		{
			if(!isAutoSoulShot)
			{
				player.sendPacket(SystemMsg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
			}
			return false;
		}

		if(pet.isDead())
		{
			if(!isAutoSoulShot)
			{
				player.sendPacket(SystemMsg.SOULSHOTS_AND_SPIRITSHOTS_ARE_NOT_AVAILABLE_FOR_A_DEAD_PET_OR_SERVITOR);
			}
			return false;
		}

		int consumption = 0;
		int skillid = 0;

		switch(item.getItemId()) {
			case 6645:
			case 20332:
				if (pet.getChargedSoulShot()) {
					return false;
				}
				consumption = pet.getSoulshotConsumeCount();
				if (!AllSettingsConfig.unlimitedShots)
					if (!player.getInventory().destroyItem(item, consumption)) {
						player.sendPacket(SystemMsg.YOU_DONT_HAVE_ENOUGH_SOULSHOTS_NEEDED_FOR_A_PETSERVITOR);
						return false;
					}
				pet.chargeSoulShot();
				skillid = 2033;
				break;
		}

		pet.broadcastPacket(new MagicSkillUse(pet, pet, skillid, 1, 0, 0));
		return true;
	}

	@Override
	public final int[] getItemIds()
	{
		return itemIds;
	}
}