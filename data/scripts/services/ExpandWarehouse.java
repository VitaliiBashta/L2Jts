package services;

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

public class ExpandWarehouse
{
	@Bypass("services.ExpandWarehouse:get")
	public void get(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null)
		{
			return;
		}

		if(!ServicesConfig.SERVICES_EXPAND_WAREHOUSE_ENABLED)
		{
			Functions.show("Сервис отключен.", player, npc);
			return;
		}

		if(ServicesConfig.SERVICES_EXPAND_WAREHOUSE_ITEM == ItemTemplate.PREMIUM_POINTS)
		{
			if(player.getPremiumAccountComponent().getPremiumPoints() >= ServicesConfig.SERVICES_EXPAND_WAREHOUSE_PRICE)
			{
				AuthServerCommunication.getInstance().sendPacket(new RequestPlayerGamePointIncrease(player, ServicesConfig.SERVICES_EXPAND_WAREHOUSE_PRICE, true));
				player.getPremiumAccountComponent().setExpandWarehouse(player.getPremiumAccountComponent().getExpandWarehouse() + 1);
				player.getPlayerVariables().set(PlayerVariables.EXPAND_WAHEHOUSE, String.valueOf(player.getPremiumAccountComponent().getExpandWarehouse()), -1);
				player.sendMessage("Warehouse capacity is now " + player.getWarehouseLimit());
			}
			else
				player.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
		}
		else if(player.getInventory().destroyItemByItemId(ServicesConfig.SERVICES_EXPAND_WAREHOUSE_ITEM, ServicesConfig.SERVICES_EXPAND_WAREHOUSE_PRICE))
		{
			player.getPremiumAccountComponent().setExpandWarehouse(player.getPremiumAccountComponent().getExpandWarehouse() + 1);
			player.getPlayerVariables().set(PlayerVariables.EXPAND_WAHEHOUSE, String.valueOf(player.getPremiumAccountComponent().getExpandWarehouse()), -1);
			player.sendMessage("Warehouse capacity is now " + player.getWarehouseLimit());
		}
		else if(ServicesConfig.SERVICES_EXPAND_WAREHOUSE_ITEM == ItemTemplate.ITEM_ID_ADENA)
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		}
		else
		{
			player.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
		}

		show(player, npc, arg);
	}

	@Bypass("services.ExpandWarehouse:show")
	public void show(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null)
		{
			return;
		}

		if(!ServicesConfig.SERVICES_EXPAND_WAREHOUSE_ENABLED)
		{
			Functions.show("Сервис отключен.", player, npc);
			return;
		}

		final String itemName = ServicesConfig.SERVICES_EXPAND_WAREHOUSE_ITEM == ItemTemplate.PREMIUM_POINTS ? "premium points" : ItemTemplateHolder.getInstance().getTemplate(ServicesConfig.SERVICES_EXPAND_WAREHOUSE_ITEM).getName();
		String out = "";

		out += "<html><body>Расширение склада";
		out += "<br><br><table>";
		out += "<tr><td>Текущий размер:</td><td>" + player.getWarehouseLimit() + "</td></tr>";
		out += "<tr><td>Стоимость слота:</td><td>" + ServicesConfig.SERVICES_EXPAND_WAREHOUSE_PRICE + ' ' + itemName + "</td></tr>";
		out += "</table><br><br>";
		out += "<button width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h htmbypass_services.ExpandWarehouse:get\" value=\"Расширить\">";
		out += "</body></html>";

		Functions.show(out, player, npc);
	}
}