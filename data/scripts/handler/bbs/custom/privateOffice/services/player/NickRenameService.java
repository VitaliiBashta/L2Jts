package handler.bbs.custom.privateOffice.services.player;
import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.configuration.config.community.CServiceConfig;
import org.mmocore.gameserver.data.client.holder.NpcNameLineHolder;
import org.mmocore.gameserver.data.xml.holder.PatternHolder;
import org.mmocore.gameserver.database.dao.impl.CharacterDAO;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.Util;

import java.io.File;
import java.nio.file.Files;

/**
 * @author Mangol
 * @since 10.02.2016
 */
public class NickRenameService extends CommunityBoardService
{
	private static String pattern;
	@Override
	public void content(final Player player, final String bypass, final Object... params)
	{
		String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/rename.htm", player);
		htm = htm.replace("<?correct_name?>", player.getName());
		htm = htm.replace("<?price?>", String.valueOf(getService().getItemCount()));
		htm = htm.replace("<?item_name?>", getItemName(player.getLanguage(), getService().getItemId()));
		separateAndSend(htm, player);
	}

	@Override
	public void request(final Player player, final String bypass, final Object... params)
	{
		final String[] str = bypass.split(":");
		if(str.length < 5)
		{
			player.sendPacket(new CustomMessage("bbs.service.nickRename.error"));
			useSaveCommand(player);
			return;
		}
		if(player.isHero())
		{
			player.sendPacket(new CustomMessage("bbs.service.nickRename.isHero"));
			useSaveCommand(player);
			return;
		}
		if(player.isClanLeader())
		{
			player.sendPacket(new CustomMessage("bbs.service.nickRename.isClanLeader"));
			useSaveCommand(player);
			return;
		}
		if(player.isTransformed())
		{
			player.sendPacket(new CustomMessage("bbs.service.nickRename.isTransformed"));
			useSaveCommand(player);
			return;
		}
		if(player.getEvent(SiegeEvent.class) != null)
		{
			player.sendPacket(new CustomMessage("bbs.service.nickRename.isEventSiege"));
			useSaveCommand(player);
			return;
		}
		final String name = bypass.split(":")[4].trim();
		String pattern = PatternHolder.getInstance().getPattern(PatternHolder.PatternType.ServiceRename);
		if(!Util.isMatchingRegexp(name, pattern != null ? pattern : ServerConfig.CNAME_TEMPLATE)
				|| NpcNameLineHolder.getInstance().isBlackListContainsName(name)
				|| !Util.checkIsAllowedName(name))
		{
			player.sendPacket(new CustomMessage("bbs.service.nickRename.notCorrectName"));
			useCommand(player, getService().getContentBypass());
			return;
		}
		if(CharacterDAO.getInstance().getObjectIdByName(name) > 0)
		{
			player.sendPacket(new CustomMessage("bbs.service.nickRename.duplicateName"));
			useCommand(player, getService().getContentBypass());
			return;
		}
		if(!getCheckAndPick(player, getService().getItemId(), getService().getItemCount(), true))
		{
			useCommand(player, getService().getContentBypass());
			return;
		}
		reply(player, name);
	}

	@Override
	public void reply(final Player player, final Object... params)
	{
		final String name = (String) params[0];
		final String oldName = player.getName();
		player.reName(name, true);
		useSaveCommand(player);
		player.sendPacket(new CustomMessage("bbs.service.successfully"));
		Log.service("NickRenameService", player, oldName + " renamed to " + name + "renames");
	}

	@Override
	public Services getService()
	{
		return Services.nick_rename;
	}

	@Override
	public void onInit() {

		super.onInit();
	}
}
