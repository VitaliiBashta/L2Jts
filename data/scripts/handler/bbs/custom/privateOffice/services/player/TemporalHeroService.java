package handler.bbs.custom.privateOffice.services.player;
import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import org.mmocore.commons.converter.Converter;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.TimeUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author Mangol
 * @since 23.02.2016
 */
public class TemporalHeroService extends CommunityBoardService
{
	@Override
	public void content(final Player player, final String bypass, final Object... params)
	{
		final int countLine = 2;
		final int countColumn = 3;
		final int countButtonMax = countColumn * countLine;
		String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/temporalHero/buy_hero.htm", player);
		String table = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/temporalHero/table.htm", player);
		String button = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/temporalHero/button.htm", player);
		int correctCount = 0;
		int correctLine = 1;
		final StringBuilder build = new StringBuilder();
		begin:
		for(; correctLine <= countLine; )
		{
			final StringBuilder buttons = new StringBuilder();
			int correctColumnWrite = 1;
			for(; correctCount <= countButtonMax; )
			{
				if(correctCount > getService().getDays().length - 1)
				{
					if(correctColumnWrite > 1 && correctColumnWrite <= countColumn)
					{
						String tableReplace = table;
						tableReplace = tableReplace.replace("<?buttons?>", buttons.toString());
						build.append(tableReplace);
					}
					break begin;
				}
				String generateButton = button;
				generateButton = generateButton.replace("<?index?>", String.valueOf(correctCount));
				generateButton = generateButton.replace("<?days?>", String.valueOf(getService().getDays()[correctCount]));
				generateButton = generateButton.replace("<?price?>", String.valueOf(getService().getItemCounts()[correctCount]));
				generateButton = generateButton.replace("<?item_name?>", getItemName(player.getLanguage(), getService().getItemIds()[correctCount]));
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
		final long timeMillis = player.getPlayerVariables().getLong(PlayerVariables.TEMPORAL_HERO);
		if(timeMillis > getSystemTime())
		{
			final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timeMillis), ZoneId.systemDefault());
			final String time = TimeUtils.dateTimeFormat(zonedDateTime);
			htm = htm.replace("<?correct_days_temporal_hero?>", new CustomMessage("bbs.service.temporalHero.correctTime").
					addString(time).
					toString(player));
		}
		else
		{
			htm = htm.replace("<?correct_days_temporal_hero?>", new CustomMessage("bbs.service.temporalHero.noTime").toString(player));
		}
		htm = htm.replace("<?tables?>", build.toString());
		separateAndSend(htm, player);
	}

	@Override
	public void request(final Player player, final String bypass, final Object... params)
	{
		if(player == null)
		{
			return;
		}
		final String[] str = bypass.split(":");
		if(str.length < 5)
		{
			player.sendPacket(new CustomMessage("bbs.service.temporalHero.error"));
			useSaveCommand(player);
			return;
		}
		if(player.isHero() || player.getCustomPlayerComponent().isTemporalHero())
		{
			player.sendPacket(new CustomMessage("bbs.service.temporalHero.isHero"));
			useSaveCommand(player);
			return;
		}
		if(!player.isInPeaceZone())
		{
			player.sendPacket(new CustomMessage("bbs.service.temporalHero.noIsInPeaceZone"));
			useSaveCommand(player);
			return;
		}
		if(player.getEvent(SiegeEvent.class) != null)
		{
			player.sendPacket(new CustomMessage("bbs.service.temporalHero.isEventSiege"));
			useSaveCommand(player);
			return;
		}
		final int index = Converter.convert(Integer.class, str[4]);
		if(index < 0 || (index >= getService().getDays().length || index >= getService().getItemIds().length ||
				index >= getService().getItemCounts().length))
		{
			player.sendPacket(new CustomMessage("bbs.service.temporalHero.error"));
			useSaveCommand(player);
			return;
		}
		if(!getCheckAndPick(player, getService().getItemIds()[index], getService().getItemCounts()[index], true))
		{
			useSaveCommand(player);
			return;
		}
		reply(player, index);
	}

	@Override
	public void reply(final Player player, final Object... params)
	{
		final int index = (int) params[0];
		final int days = getService().getDays()[index];
		final ZonedDateTime time = ZonedDateTime.now(ZoneId.systemDefault()).plusDays(days);
		player.getPlayerVariables().set(PlayerVariables.TEMPORAL_HERO, time.toInstant().toEpochMilli(), time.toInstant().toEpochMilli());
		player.getCustomPlayerComponent().startTemporalHero();
		useSaveCommand(player);
		player.sendPacket(new CustomMessage("bbs.service.successfully"));
		Log.service("TemporalHeroService", player, "buy hero days - " + days);
	}

	@Override
	public Services getService()
	{
		return Services.temporal_hero;
	}

}
