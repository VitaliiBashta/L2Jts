package services;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.data.xml.holder.MultiSellHolder;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.handler.npcdialog.INpcDialogAppender;
import org.mmocore.gameserver.handler.npcdialog.NpcDialogAppenderHolder;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.model.MultiSellListContainer;
import org.mmocore.gameserver.model.base.MultiSellEntry;
import org.mmocore.gameserver.model.base.MultiSellIngredient;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.item.ItemTemplate;

public class Pushkin implements INpcDialogAppender, OnInitScriptListener
{
	@Override
	public String getAppend(Player player, NpcInstance npc, int val)
	{
		if(player == null || npc == null || val != 0)
		{
			return "";
		}

		if(!AllSettingsConfig.ALT_SIMPLE_SIGNS && !AllSettingsConfig.ALT_BS_CRYSTALLIZE)
		{
			return "";
		}

		StringBuilder append = new StringBuilder();

		if(player.isLangRus())
		{
			if(AllSettingsConfig.ALT_SIMPLE_SIGNS)
			{
				append.append("<br><center>Опции семи печатей:</center><br>");
				append.append("<center>[npc_%objectId%_Multisell 3112605|Сделать S-грейд меч]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 3112606|Вставить SA в оружие S-грейда]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 3112607|Распечатать броню S-грейда]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 3112608|Распечатать бижутерию S-грейда]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 3112609|Сделать A-грейд меч]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 3112610|Вставить SA в оружие A-грейда]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 3112611|Распечатать броню A-грейда]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 3112612|Распечатать бижутерию A-грейда]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 3112613|Запечатать броню A-грейда]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 3112601|Удалить SA из оружия]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 3112602|Обменять оружие с доплатой]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 3112603|Обменять оружие на равноценное]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 3112604|Завершить редкую вещь]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 3111301|Купить что-нибудь]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 400|Обменять камни]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 500|Приобрести расходные материалы]</center><br1>");
			}
			if(AllSettingsConfig.ALT_BS_CRYSTALLIZE)// TODO: сделать у всех кузнецов
			{
				append.append("<br1>[npc_%objectId%_services.Pushkin:doCrystallize|Кристаллизация]");
			}
		}
		else
		{
			if(AllSettingsConfig.ALT_SIMPLE_SIGNS)
			{
				append.append("<br><center>Seven Signs options:</center><br>");
				append.append("<center>[npc_%objectId%_Multisell 3112605|Manufacture an S-grade sword]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 3112606|Bestow the special S-grade weapon some abilities]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 3112607|Release the S-grade armor seal]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 3112608|Release the S-grade accessory seal]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 3112609|Manufacture an A-grade sword]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 3112610|Bestow the special A-grade weapon some abilities]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 3112611|Release the A-grade armor seal]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 3112612|Release the A-grade accessory seal]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 3112613|Seal the A-grade armor again]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 3112601|Remove the special abilities from a weapon]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 3112602|Upgrade weapon]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 3112603|Make an even exchange of weapons]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 3112604|Complete a Foundation Item]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 3111301|Buy Something]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 400|Exchange Seal Stones]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 500|Purchase consumable items]</center><br1>");
			}
			if(AllSettingsConfig.ALT_BS_CRYSTALLIZE)
			{
				append.append("<br1>[npc_%objectId%_services.Pushkin:doCrystallize|Crystallize]");
			}
		}

		return append.toString();
	}

	@Bypass("services.Pushkin:doCrystallize")
	public void doCrystallize(Player player, NpcInstance npc, String[] arg)
	{
		NpcInstance merchant = player.getLastNpc();
		Castle castle = merchant != null ? merchant.getCastle(player) : null;

		MultiSellListContainer list = new MultiSellListContainer();
		list.setShowAll(false);
		list.setKeepEnchant(true);
		list.setNoTax(false);
		int entry = 0;
		final Inventory inv = player.getInventory();
		for(final ItemInstance itm : inv.getItems())
		{
			if(itm.canBeCrystallized(player))
			{
				final ItemTemplate crystal = ItemTemplateHolder.getInstance().getTemplate(itm.getTemplate().getCrystalType().cry);
				final int crystalCount = itm.getTemplate().getCrystalCount(itm.getEnchantLevel(), false);
				MultiSellEntry possibleEntry = new MultiSellEntry(++entry, crystal.getItemId(), crystalCount, 0);
				possibleEntry.addIngredient(new MultiSellIngredient(itm.getItemId(), 1, itm.getEnchantLevel()));
				possibleEntry.addIngredient(new MultiSellIngredient(ItemTemplate.ITEM_ID_ADENA, Math.round(crystalCount * crystal.getReferencePrice() * 0.05), 0));
				list.addEntry(possibleEntry);
			}
		}

		MultiSellHolder.getInstance().SeparateAndSend(list, player, merchant != null ? merchant.getObjectId() : -1, castle == null ? 0. : castle.getTaxRate());
	}

	@Override
	public void onInit()
	{
		NpcDialogAppenderHolder.getInstance().register(this);
	}

	@Override
	public int[] getNpcIds()
	{
		return new int[] { 30300, 30086, 30098 };
	}
}