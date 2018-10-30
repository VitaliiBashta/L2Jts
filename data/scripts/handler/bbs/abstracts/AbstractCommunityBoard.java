package handler.bbs.abstracts;
import handler.bbs.dao.service.CommunityServiceDAO;
import handler.bbs.object.CommunityComponent;
import org.mmocore.commons.converter.Converter;
import org.mmocore.gameserver.data.client.holder.ItemNameLineHolder;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.handler.bbs.BbsHandlerHolder;
import org.mmocore.gameserver.handler.bbs.IBbsHandler;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.gs2as.RequestPlayerGamePointIncrease;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ShowBoard;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.client.ItemNameLine;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.Language;
import org.mmocore.gameserver.utils.ThymeleafJob;
import org.mmocore.gameserver.utils.Util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Stack;

/**
 * @author Mangol
 * @since 18.02.2016
 */
public abstract class AbstractCommunityBoard extends Converter
{
	private static final String bbsHome = "_bbshome";
	protected static final CommunityServiceDAO serviceDAO = CommunityServiceDAO.getInstance();
	/**
	 * Метод для вызова страницы коммунки.
	 * @param player - игрок
	 * @param link - бипасс который будем использовать
	 */
	public static void useCommand(final Player player, final String link)
	{
		final Optional<IBbsHandler> handler = BbsHandlerHolder.getInstance().getCommunityHandler(link);
		if(handler.isPresent())
		{
			handler.get().onBypassCommand(player, link);
		}
	}

	/**
	 * Метод, который сохраняет ссылку бипасса игрока.
	 * При использовании данного метода, обязательно нужно чтобы сработал где-то useSaveCommand метод, не нужно забывать про него.
	 *
	 * @param player     - игрок
	 * @param bypass     - бипасс игрока
	 * @param stackClear - очистка стека
	 */
	public static void saveCommand(final Player player, final String bypass, final boolean stackClear)
	{
		final CommunityComponent communityComponent = (CommunityComponent) player.getCommunityComponent();
		if(communityComponent != null)
		{
			final Stack<String> stackBypass = player.getCommunityComponent().getStackBypass();
			if(stackClear)
			{
				stackBypass.clear();
				stackBypass.push(bypass);
				return;
			}
			int searchCount = stackBypass.search(bypass);
			if(searchCount == -1)
			{
				stackBypass.push(bypass);
				return;
			}
			while(searchCount > 1)
			{
				stackBypass.pop();
				searchCount--;
			}
		}
	}

	/**
	 * @param player        - игрок
	 * @param defaultBypass - дефолтный бипасс если стек пуст.
	 */
	public static void useSaveCommand(final Player player, final String defaultBypass)
	{
		String bypass = defaultBypass;
		final CommunityComponent communityComponent = (CommunityComponent) player.getCommunityComponent();
		if(communityComponent != null)
		{
			final Stack<String> stackBypass = player.getCommunityComponent().getStackBypass();
			if(!stackBypass.isEmpty())
			{
				bypass = stackBypass.pop();
			}
		}
		final Optional<IBbsHandler> handler = BbsHandlerHolder.getInstance().getCommunityHandler(bypass);
		if(handler.isPresent())
		{
			handler.get().onBypassCommand(player, bypass);
		}
	}

	/**
	 * Если стек пуст использует главную страницу коммунити.
	 *
	 * @param player - игрок
	 */
	public static void useSaveCommand(final Player player)
	{
		useSaveCommand(player, bbsHome);
	}

	protected static HtmCache getCache()
	{
		return HtmCache.getInstance();
	}

	protected static ThymeleafJob thymeleafJob()
	{
		return ThymeleafJob.getInstance();
	}

	protected static String getItemName(final Language language, final int itemId)
	{
		final Optional<ItemNameLine> itemNameLine = Optional.ofNullable(ItemNameLineHolder.getInstance().get(language, itemId));
		if(itemNameLine.isPresent())
		{
			return itemNameLine.get().getName();
		}
		return language == Language.ENGLISH ? "no item name" : "без имени";
	}

	public static boolean getPay(final Player player, final int itemid, final long count, final boolean sendMessage)
	{
		if(count == 0)
		{
			return true;
		}
		if(player.getInventory().getCountOf(itemid) >= count)
		{
			player.getInventory().destroyItemByItemId(itemid, count);
		}
		else
		{
			if(sendMessage)
			{
				enoughtItem(player, itemid, count);
			}
			return false;
		}
		if(sendMessage)
		{
			player.sendMessage(new CustomMessage("util.getpay").addString(formatPay(player, count, itemid)));
		}
		return true;
	}

	public static boolean getCheckAndPick(final Player player, final int itemid, final long count, final boolean sendMessage)
	{
		if(count == 0)
		{
			return true;
		}
		if(itemid == ItemTemplate.PREMIUM_POINTS)
		{
			if(player.getPremiumAccountComponent().getPremiumPoints() >= count)
			{
				AuthServerCommunication.getInstance().sendPacket(new RequestPlayerGamePointIncrease(player, count, true));
			}
		}
		else if(player.getInventory().getCountOf(itemid) >= count)
		{
			player.getInventory().destroyItemByItemId(itemid, count);
		}
		else
		{
			if(sendMessage)
			{
				enoughtItem(player, itemid, count);
			}
			return false;
		}
		if(sendMessage)
		{
			player.sendMessage(new CustomMessage("util.getpay").addString(formatPay(player, count, itemid)));
		}
		return true;
	}

	protected static void enoughtItem(final Player player, final int itemid, final long count)
	{
		player.sendPacket(new ExShowScreenMessage(new CustomMessage("util.enoughItemCount").addString(formatPay(player, count, itemid)).toString(player), 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, false));
		player.sendMessage(new CustomMessage("util.enoughItemCount").addString(formatPay(player, count, itemid)));
	}

	protected static String formatPay(final Player player, final long count, final int item)
	{
		return Util.formatPay(player, count, item);
	}

	protected static void separateAndSend(final String html, final Player player)
	{
		ShowBoard.separateAndSend(html, player);
	}

	protected static String className(Player player, int classId)
	{
		if((classId < 0 || classId > 136) || (classId > 118 && classId < 123) || (classId > 57 && classId < 88))
		{
			return new CustomMessage("utils.classId.name.default").toString(player);
		}
		return new CustomMessage("utils.classId.name." + classId).toString(player);
	}

	protected static long getSystemTime()
	{
		return ZonedDateTime.now(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
}
