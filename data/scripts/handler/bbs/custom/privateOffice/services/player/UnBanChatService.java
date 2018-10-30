package handler.bbs.custom.privateOffice.services.player;
import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.TimeUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author Mangol
 * @since 21.02.2016
 */
public class UnBanChatService extends CommunityBoardService
{
	@Override
	public void content(final Player player, final String bypass, final Object... params)
	{
		String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/unban_chat.htm", player);
		if(player.getNoChannelRemained() > 0)
		{
			final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.now().plusMillis(player.getNoChannelRemained()), ZoneId.systemDefault());
			final String time = TimeUtils.dateTimeFormat(zonedDateTime);
			htm = htm.replace("<?correct_ban?>", new CustomMessage("bbs.service.unBanChat.banTime").
					addString(time).
					toString(player));
		}
		else if(player.getNoChannelRemained() < 0)
		{
			htm = htm.replace("<?correct_ban?>", new CustomMessage("bbs.service.unBanChat.banPermanently").toString(player));
		}
		else
		{
			htm = htm.replace("<?correct_ban?>", new CustomMessage("bbs.service.unBanChat.noBan").toString(player));
		}
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
		if(player.getNoChannelRemained() == 0)
		{
			player.sendPacket(new CustomMessage("bbs.service.unBanChat.noBan"));
			useSaveCommand(player);
			return;
		}
		if(player.getNoChannelRemained() < 0)
		{
			player.sendPacket(new CustomMessage("bbs.service.unBanChat.banPermanently"));
			useSaveCommand(player);
			return;
		}
		if(!getCheckAndPick(player, getService().getItemId(), getService().getItemCount(), true))
		{
			useSaveCommand(player);
			return;
		}
		reply(player);
	}

	@Override
	public void reply(final Player player, final Object... params)
	{
		player.updateNoChannel(0);
		useSaveCommand(player);
		player.sendPacket(new CustomMessage("bbs.service.successfully"));
		Log.service("UnBanChatService", player, "char un ban chat");
	}

	@Override
	public Services getService()
	{
		return Services.unban_chat;
	}

}
