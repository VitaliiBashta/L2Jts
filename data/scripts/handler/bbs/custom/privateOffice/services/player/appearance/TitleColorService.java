package handler.bbs.custom.privateOffice.services.player.appearance;
import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import org.mmocore.commons.converter.Converter;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.data.xml.holder.other.ColorHolder;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.AppearanceComponent;
import org.mmocore.gameserver.templates.other.ColorTemplate;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.Util;

/**
 * @author Mangol
 * @since 21.02.2016
 */
public class TitleColorService extends CommunityBoardService
{
	private static final ColorHolder colorHolder = ColorHolder.getInstance();

	@Override
	public void content(final Player player, final String bypass, final Object... params)
	{
		final String[] str = bypass.split(":");
		if(str.length < 5)
		{
			player.sendPacket(new CustomMessage("bbs.service.titleColor.contentError"));
			useSaveCommand(player);
			return;
		}
		final int page = Converter.convert(Integer.class, str[4]);
		final int countLine = 4;
		final int countColumn = 4;
		final int countColor = countColumn * countLine;
		final int minPageColor = (page * countColor) - countColor;
		final int maxPageColor = page * countColor;
		String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/color/title_color.htm", player);
		String table = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/color/table.htm", player);
		String button = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/color/button.htm", player);
		int correctCount = minPageColor;
		int correctLine = 1;
		final StringBuilder build = new StringBuilder();
		begin:
		for(; correctLine <= countLine; )
		{
			final StringBuilder buttons = new StringBuilder();
			int correctColumnWrite = 1;
			for(; correctCount <= maxPageColor; )
			{
				if(correctCount > colorHolder.getListColor(getService().name()).size() - 1)
				{
					if(correctColumnWrite > 1 && correctColumnWrite <= countColumn)
					{
						for(; correctColumnWrite <= countColumn; correctColumnWrite++)
						{
							buttons.append(button.replace("<?color?>", ""));
						}
						String tableReplace = table;
						tableReplace = tableReplace.replace("<?buttons?>", buttons.toString());
						build.append(tableReplace);
					}
					break begin;
				}
				final ColorTemplate color = colorHolder.getColorTemplate(getService().name(), correctCount);
				if(color == null)
				{
					break begin;
				}
				final String generateButton = button.replace("<?color?>", "<a action=\"bypass _bbsservice:service:title_color:request:" + correctCount + "\"><font color=\"" + color.getColor() + "\">" + color.getName(player) + "</font></a><br>");
				buttons.append(generateButton);
				correctCount++;
				// Находимся на последнем столбце, переходим на следующую строку.
				if(correctColumnWrite == countColumn)
				{
					String tableReplace = table;
					tableReplace = tableReplace.replace("<?buttons?>", buttons.toString());
					build.append(tableReplace);
					correctLine++;
					continue begin;
				}
				correctColumnWrite++;
			}
		}
		htm = htm.replace("<?price?>", String.valueOf(getService().getItemCount()));
		htm = htm.replace("<?item_name?>", getItemName(player.getLanguage(), getService().getItemId()));
		htm = htm.replace("<?tables?>", build.toString());
		htm = htm.replace("<?page?>", String.valueOf(page));
		htm = htm.replace("<?nextPage?>", colorHolder.getListColor(getService().name()).size() > maxPageColor ? String.valueOf(page + 1) : String.valueOf(page));
		htm = htm.replace("<?backPage?>", String.valueOf(page > 1 ? page - 1 : page));
		separateAndSend(htm, player);
	}

	@Override
	public void request(final Player player, final String bypass, final Object... params)
	{
		final String[] str = bypass.split(":");
		if(str.length < 5)
		{
			player.sendPacket(new CustomMessage("bbs.service.titleColor.error"));
			useSaveCommand(player);
			return;
		}
		if(str[4].equalsIgnoreCase("reset"))
		{
			if(player.getAppearanceComponent().getNameColor() != AppearanceComponent.DEFAULT_TITLE_COLOR)
			{
				reply(player, "FFFFFF");
				return;
			}
			else
			{
				player.sendMessage(new CustomMessage("bbs.service.titleColor.standartColor"));
				useSaveCommand(player);
				return;
			}
		}
		final int colorIndex = convert(Integer.class, str[4]);
		final ColorTemplate color = colorHolder.getColorTemplate(getService().name(), colorIndex);
		if(color == null)
		{
			player.sendPacket(new CustomMessage("bbs.service.titleColor.error"));
			useSaveCommand(player);
			return;
		}
		if(!getCheckAndPick(player, getService().getItemId(), getService().getItemCount(), true))
		{
			useSaveCommand(player);
			return;
		}
		reply(player, color.getColor());
	}

	@Override
	public void reply(final Player player, final Object... params)
	{
		final String color = (String) params[0];
		player.getAppearanceComponent().setTitleColor(Integer.decode("0x" + Util.RGBtoBGR(color)));
		player.broadcastUserInfo(true);
		useSaveCommand(player);
		player.sendPacket(new CustomMessage("bbs.service.successfully"));
		Log.service("TitleColorService", player, "changed name color to " + color + ".");
	}

	@Override
	public Services getService()
	{
		return Services.title_color;
	}

}