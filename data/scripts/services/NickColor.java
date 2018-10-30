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
import org.mmocore.gameserver.templates.item.ItemTemplate;

public class NickColor
{
	@Bypass("services.NickColor:list")
	public void list(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null)
		{
			return;
		}

		StringBuilder append = new StringBuilder();
		final String itemName = ServicesConfig.SERVICES_CHANGE_NICK_COLOR_ITEM == ItemTemplate.PREMIUM_POINTS ? "premium points" : ItemTemplateHolder.getInstance().getTemplate(ServicesConfig.SERVICES_CHANGE_NICK_COLOR_ITEM).getName();
		append.append("Цена смены цвета ника ").append(ServicesConfig.SERVICES_CHANGE_NICK_COLOR_PRICE).append(' ').append(itemName).append('.');
		append.append("<br>Доступные цвета:<br>");
		for(String color : ServicesConfig.SERVICES_CHANGE_NICK_COLOR_LIST)
		{
			append.append("<br><a action=\"bypass -h htmbypass_services.NickColor:change ").append(color).append("\"><font color=\"").append(color.substring(4, 6) + color.substring(2, 4) + color.substring(0, 2)).append("\">").append(color.substring(4, 6) + color.substring(2, 4) + color.substring(0, 2)).append("</font></a>");
		}
		append.append("<br><a action=\"bypass -h htmbypass_services.NickColor:change FFFFFF\"><font color=\"FFFFFF\">Вернуть белый ник (бесплатно)</font></a>");
		Functions.show(append.toString(), player, null);
	}

	@Bypass("services.NickColor:change")
	public void change(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null || arg == null || arg.length < 1)
		{
			return;
		}

		if(arg[0].equalsIgnoreCase("FFFFFF"))
		{
			player.getAppearanceComponent().setNameColor(Integer.decode("0xFFFFFF"));
			player.broadcastUserInfo(true);
			return;
		}

		if(ServicesConfig.SERVICES_CHANGE_NICK_COLOR_ITEM == ItemTemplate.PREMIUM_POINTS)
		{
			if(player.getPremiumAccountComponent().getPremiumPoints() >= ServicesConfig.SERVICES_CHANGE_NICK_COLOR_PRICE)
			{
				AuthServerCommunication.getInstance().sendPacket(new RequestPlayerGamePointIncrease(player, ServicesConfig.SERVICES_CHANGE_NICK_COLOR_PRICE, true));
				player.getAppearanceComponent().setNameColor(Integer.decode("0x" + arg[0]));
				player.broadcastUserInfo(true);
			}
			else
				player.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
		}
		else if(player.getInventory().destroyItemByItemId(ServicesConfig.SERVICES_CHANGE_NICK_COLOR_ITEM, ServicesConfig.SERVICES_CHANGE_NICK_COLOR_PRICE))
		{
			player.getAppearanceComponent().setNameColor(Integer.decode("0x" + arg[0]));
			player.broadcastUserInfo(true);
		}
		else if(ServicesConfig.SERVICES_CHANGE_NICK_COLOR_ITEM == 57)
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		}
		else
		{
			player.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
		}
	}
}