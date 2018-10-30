package handler.bbs.custom.privateOffice.services.player;
import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import org.mmocore.commons.converter.Converter;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.configuration.config.community.CServiceConfig;
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
 * @since 22.02.2016
 */
public class NoDropPKService extends CommunityBoardService
{
	@Override
	public void content(final Player player, final String bypass, final Object... params)
	{
		String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/no_drop_pk.htm", player);
		final long timeMillis = player.getPlayerVariables().getLong(PlayerVariables.NO_DROP_PK, 0);
		if(timeMillis > getSystemTime())
		{
			final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timeMillis), ZoneId.systemDefault());
			final String time = TimeUtils.dateTimeFormat(zonedDateTime);
			htm = htm.replace("<?correct_days_no_drop_pk?>", new CustomMessage("bbs.service.noPKDrop.correctTime").
					addString(time).
					toString(player));
		}
		else
		{
			htm = htm.replace("<?correct_days_no_drop_pk?>", new CustomMessage("bbs.service.noPKDrop.noTime").toString(player));
		}
		htm = htm.replace("<?max_days_no_drop_pk?>", String.valueOf(getNoDropPKMaxDay()));
		htm = htm.replace("<?price?>", String.valueOf(getService().getItemCount()));
		htm = htm.replace("<?item_name?>", getItemName(player.getLanguage(), getService().getItemId()));
		separateAndSend(htm, player);
	}

	@Override
	public void request(final Player player, final String bypass, final Object... params)
	{
		if(player == null)
		{
			return;
		}
		final long timeMillis = player.getPlayerVariables().getLong(PlayerVariables.NO_DROP_PK);
		if(timeMillis > getSystemTime())
		{
			player.sendPacket(new CustomMessage("bbs.service.noPKDrop.activete"));
			useSaveCommand(player);
			return;
		}
		final String[] str = bypass.split(":");
		if(str.length < 5)
		{
			player.sendPacket(new CustomMessage("bbs.service.noPKDrop.error"));
			useSaveCommand(player);
			return;
		}
		if(player.getEvent(SiegeEvent.class) != null)
		{
			player.sendPacket(new CustomMessage("bbs.service.noPKDrop.isEventSiege"));
			useSaveCommand(player);
			return;
		}
		if(!player.isInPeaceZone())
		{
			player.sendPacket(new CustomMessage("bbs.service.noPKDrop.isInPeaceZone"));
			useSaveCommand(player);
			return;
		}
		final int countDays = Converter.convert(Integer.class, str[4].trim());
		final int correctCountDays = Math.min(getNoDropPKMaxDay(), countDays);
		final long price = getService().getItemCount() * correctCountDays;
		if(!getCheckAndPick(player, getService().getItemId(), price, true))
		{
			useSaveCommand(player);
			return;
		}
		reply(player, correctCountDays);
	}

	@Override
	public void reply(final Player player, final Object... params)
	{
		final int count = (int) params[0];
		final ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.systemDefault()).plusDays(count);
		//TODO: посадить на авто трид вириаблесов когда сделаем.
		player.getPlayerVariables().set(PlayerVariables.NO_DROP_PK, zonedDateTime.toInstant().toEpochMilli(), zonedDateTime.toInstant().toEpochMilli());
		useSaveCommand(player);
		player.sendPacket(new CustomMessage("bbs.service.successfully"));
		Log.service("NoDropPKService", player, "buy no drop pk days = " + count);
	}

	private int getNoDropPKMaxDay()
	{
		return CServiceConfig.noDropMaxDays;
	}

	@Override
	public Services getService()
	{
		return Services.no_drop_pk;
	}

}
