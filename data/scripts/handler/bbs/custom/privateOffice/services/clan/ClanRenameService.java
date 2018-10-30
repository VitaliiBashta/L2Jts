package handler.bbs.custom.privateOffice.services.clan;
import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.pledge.SubUnit;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.ClanTable;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.Util;

/**
 * @author Mangol
 * @since 10.02.2016
 */
public class ClanRenameService extends CommunityBoardService
{
	@Override
	public void content(final Player player, final String bypass, final Object... params)
	{
		String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/clan/clan_rename.htm", player);
		if(player.getClan() == null)
		{
			htm = htm.replace("<?correct_name_clan?>", new CustomMessage("bbs.service.clanRename.noClan").toString(player));
		}
		else if(!player.isClanLeader())
		{
			htm = htm.replace("<?correct_name_clan?>", new CustomMessage("bbs.service.clanRename.notLeader").toString(player));
		}
		else
		{
			htm = htm.replace("<?correct_name_clan?>", player.getClan().getName());
		}
		htm = htm.replace("<?price?>", String.valueOf(getService().getItemCount()));
		htm = htm.replace("<?item_name?>", getItemName(player.getLanguage(), getService().getItemId()));
		separateAndSend(htm, player);
	}

	@Override
	public void request(Player player, String bypass, Object... params)
	{
		final String[] str = bypass.split(":");
		if(str.length < 5)
		{
			player.sendPacket(new CustomMessage("bbs.service.clanRename.error"));
			useSaveCommand(player);
			return;
		}
		if(player.getEvent(SiegeEvent.class) != null)
		{
			player.sendPacket(new CustomMessage("bbs.service.clanRename.isEventSiege"));
			useSaveCommand(player);
			return;
		}
		if(player.getClan() == null)
		{
			player.sendPacket(new CustomMessage("bbs.service.clanRename.noClan"));
			useSaveCommand(player);
			return;
		}
		if(!player.isClanLeader())
		{
			player.sendPacket(new CustomMessage("bbs.service.clanRename.notLeader"));
			useSaveCommand(player);
			return;
		}
		final String newName = bypass.split(":")[4].trim();
		if(!Util.isMatchingRegexp(newName, ServerConfig.CLAN_NAME_TEMPLATE))
		{
			player.sendPacket(new CustomMessage("bbs.service.clanRename.notCorrectName"));
			useCommand(player, getService().getContentBypass());
			return;
		}
		if(ClanTable.getInstance().getClanByName(newName) != null)
		{
			player.sendPacket(new CustomMessage("bbs.service.clanRename.duplicateName"));
			useCommand(player, getService().getContentBypass());
			return;
		}
		for(final SubUnit unit : player.getClan().getAllSubUnits())
		{
			if(unit != null && unit.getName().equals(newName))
			{
				player.sendPacket(new CustomMessage("bbs.service.clanRename.duplicateName"));
				useCommand(player, getService().getContentBypass());
				return;
			}
		}
		if(!getCheckAndPick(player, getService().getItemId(), getService().getItemCount(), true))
		{
			useSaveCommand(player);
			return;
		}
		reply(player, newName);
	}

	@Override
	public void reply(final Player player, final Object... params)
	{
		final String name = (String) params[0];
		final SubUnit sub = player.getClan().getSubUnit(Clan.SUBUNIT_MAIN_CLAN);
		final String oldName = sub.getName();
		sub.setName(name, true);
		player.getClan().broadcastClanStatus(true, true, false);
		useSaveCommand(player);
		player.sendPacket(new CustomMessage("bbs.service.successfully"));
		Log.service("ClanRenameService", player, "rename clan " + oldName + " to new " + name + " renames");
	}

	@Override
	public Services getService()
	{
		return Services.clan_rename;
	}

}
