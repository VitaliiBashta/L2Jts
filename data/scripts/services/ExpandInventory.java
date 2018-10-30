package services;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.gameserver.configuration.config.OtherConfig;
import org.mmocore.gameserver.configuration.config.ServicesConfig;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.gs2as.RequestPlayerGamePointIncrease;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.templates.item.ItemTemplate;

public class ExpandInventory
{
	@Bypass("services.ExpandInventory:get")
	public void get(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null)
		{
			return;
		}

		if(!ServicesConfig.SERVICES_EXPAND_INVENTORY_ENABLED)
		{
			Functions.show("Сервис отключен.", player, npc);
			return;
		}
		final int b = player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.dwarf ? OtherConfig.INVENTORY_MAX_FOR_DWARF : OtherConfig.INVENTORY_MAX_NO_DWARF;
		if(player.getInventoryLimit() >= b)
		{
			player.sendMessage("Already max count.");
			return;
		}

		if(ServicesConfig.SERVICES_EXPAND_INVENTORY_ITEM == ItemTemplate.PREMIUM_POINTS)
		{
			if(player.getPremiumAccountComponent().getPremiumPoints() >= ServicesConfig.SERVICES_EXPAND_INVENTORY_PRICE)
			{
				AuthServerCommunication.getInstance().sendPacket(new RequestPlayerGamePointIncrease(player, ServicesConfig.SERVICES_EXPAND_INVENTORY_PRICE, true));
				player.getPremiumAccountComponent().setExpandInventory(player.getPremiumAccountComponent().getExpandInventory() + 1);
				player.getPlayerVariables().set(PlayerVariables.EXPAND_INVENTORY, String.valueOf(player.getPremiumAccountComponent().getExpandInventory()), -1);
				player.sendMessage("Inventory capacity is now " + player.getInventoryLimit());
			}
			else
				player.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
		}

		else if(player.getInventory().destroyItemByItemId(ServicesConfig.SERVICES_EXPAND_INVENTORY_ITEM, ServicesConfig.SERVICES_EXPAND_INVENTORY_PRICE))
		{
			player.getPremiumAccountComponent().setExpandInventory(player.getPremiumAccountComponent().getExpandInventory() + 1);
			player.getPlayerVariables().set(PlayerVariables.EXPAND_INVENTORY, String.valueOf(player.getPremiumAccountComponent().getExpandInventory()), -1);
			player.sendMessage("Inventory capacity is now " + player.getInventoryLimit());
		}
		else if(ServicesConfig.SERVICES_EXPAND_INVENTORY_ITEM == ItemTemplate.ITEM_ID_ADENA)
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		}
		else
		{
			player.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
		}

		show(player, npc, arg);
	}

	@Bypass("services.ExpandInventory:show")
	public void show(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null)
		{
			return;
		}

		if(!ServicesConfig.SERVICES_EXPAND_INVENTORY_ENABLED)
		{
			Functions.show("Сервис отключен.", player, npc);
			return;
		}
		final int b = player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.dwarf ? OtherConfig.INVENTORY_MAX_FOR_DWARF : OtherConfig.INVENTORY_MAX_NO_DWARF;

		final String itemName = ServicesConfig.SERVICES_EXPAND_INVENTORY_ITEM == ItemTemplate.PREMIUM_POINTS ? "premium points" : ItemTemplateHolder.getInstance().getTemplate(ServicesConfig.SERVICES_EXPAND_INVENTORY_ITEM).getName();

		String out = "";

		out += "<html><body>Расширение инвентаря";
		out += "<br><br><table>";
		out += "<tr><td>Текущий размер:</td><td>" + player.getInventoryLimit() + "</td></tr>";
		out += "<tr><td>Максимальный размер:</td><td>" + b + "</td></tr>";
		out += "<tr><td>Стоимость слота:</td><td>" + ServicesConfig.SERVICES_EXPAND_INVENTORY_PRICE + ' ' + itemName + "</td></tr>";
		out += "</table><br><br>";
		out += "<button width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h htmbypass_services.ExpandInventory:get\" value=\"Расширить\">";
		out += "</body></html>";

		Functions.show(out, player, npc);
	}
}