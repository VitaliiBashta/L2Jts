package services;

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
import org.mmocore.gameserver.templates.item.ItemTemplate;

public class ExpandCWH
{
	@Bypass("services.ExpandCWH:get")
	public void get(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null)
		{
			return;
		}

		if(!ServicesConfig.SERVICES_EXPAND_CWH_ENABLED)
		{
			Functions.show("Сервис отключен.", player, npc);
			return;
		}

		if(player.getClan() == null)
		{
			player.sendMessage("You must be in clan.");
			return;
		}

		if(ServicesConfig.SERVICES_EXPAND_CWH_ITEM == ItemTemplate.PREMIUM_POINTS)
		{
			if(player.getPremiumAccountComponent().getPremiumPoints() >= ServicesConfig.SERVICES_EXPAND_CWH_PRICE)
			{
				AuthServerCommunication.getInstance().sendPacket(new RequestPlayerGamePointIncrease(player, ServicesConfig.SERVICES_EXPAND_CWH_PRICE, true));
				player.getClan().setWarehouseBonus(player.getClan().getWarehouseBonus() + 1);
				player.sendMessage("Warehouse capacity is now " + (OtherConfig.WAREHOUSE_SLOTS_CLAN + player.getClan().getWarehouseBonus()));
			}
			else
				player.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
		}

		else if(player.getInventory().destroyItemByItemId(ServicesConfig.SERVICES_EXPAND_CWH_ITEM, ServicesConfig.SERVICES_EXPAND_CWH_PRICE))
		{
			player.getClan().setWarehouseBonus(player.getClan().getWarehouseBonus() + 1);
			player.sendMessage("Warehouse capacity is now " + (OtherConfig.WAREHOUSE_SLOTS_CLAN + player.getClan().getWarehouseBonus()));
		}
		else if(ServicesConfig.SERVICES_EXPAND_CWH_ITEM == ItemTemplate.ITEM_ID_ADENA)
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		}
		else
		{
			player.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
		}

		show(player, npc, arg);
	}

	@Bypass("services.ExpandCWH:show")
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

		if(player.getClan() == null)
		{
			player.sendMessage("You must be in clan.");
			return;
		}

		final String itemName = ServicesConfig.SERVICES_EXPAND_CWH_ITEM == ItemTemplate.PREMIUM_POINTS ? "premium points" : ItemTemplateHolder.getInstance().getTemplate(ServicesConfig.SERVICES_EXPAND_CWH_ITEM).getName();

		String out = "";

		out += "<html><body>Расширение кланового склада";
		out += "<br><br><table>";
		out += "<tr><td>Текущий размер:</td><td>" + (OtherConfig.WAREHOUSE_SLOTS_CLAN + player.getClan().getWarehouseBonus()) + "</td></tr>";
		out += "<tr><td>Стоимость слота:</td><td>" + ServicesConfig.SERVICES_EXPAND_CWH_PRICE + ' ' + itemName + "</td></tr>";
		out += "</table><br><br>";
		out += "<button width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h htmbypass_services.ExpandCWH:get\" value=\"Расширить\">";
		out += "</body></html>";

		Functions.show(out, player, npc);
	}
}
