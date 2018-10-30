package handler.bbs.custom.privateOffice.services.player.appearance;
import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Log;

import java.util.Optional;

/**
 * @author Mangol
 * @since 27.02.2016
 */
public class ChangeRaceService extends CommunityBoardService
{
	@Override
	public Services getService()
	{
		return Services.change_race;
	}

	@Override
	public void content(final Player player, final String bypass, final Object... params)
	{
		String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/changeRace/change_race.htm", player);
		String race = "";
		String listsStr = "";
		switch(player.getPlayerTemplateComponent().getPlayerRace())
		{
			case human:
				race = new CustomMessage("bbs.service.changeRace.human").toString(player);
				listsStr = new CustomMessage("bbs.service.changeRace.noChangeRaceHuman").toString(player);
				break;
			case elf:
				race = new CustomMessage("bbs.service.changeRace.elf").toString(player);
				break;
			case darkelf:
				race = new CustomMessage("bbs.service.changeRace.darkelf").toString(player);
				break;
			case orc:
				race = new CustomMessage("bbs.service.changeRace.orc").toString(player);
				listsStr = new CustomMessage("bbs.service.changeRace.noChangeRaceOrk").toString(player);
				break;
			case dwarf:
				race = new CustomMessage("bbs.service.changeRace.dwarf").toString(player);
				break;
			case kamael:
				race = new CustomMessage("bbs.service.changeRace.kamael").toString(player);
				listsStr = new CustomMessage("bbs.service.changeRace.noChangeRaceKamael").toString(player);
				break;
		}
		if(listsStr.isEmpty())
		{
			for(final PlayerRace playerRace : PlayerRace.values())
			{
				if(playerRace != player.getPlayerTemplateComponent().getPlayerRace() && playerRace != PlayerRace.kamael && playerRace != PlayerRace.human && playerRace != PlayerRace.orc)
				{
					listsStr += getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/changeRace/" + playerRace.name() + ".htm", player);
				}
			}
		}
		else
		{
			String text = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/changeRace/text.htm", player);
			text = text.replace("<?text?>", listsStr);
			listsStr = text;
		}
		htm = htm.replace("<?lists?>", listsStr);
		htm = htm.replace("<?correct_race?>", race);
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
		final String[] str = bypass.split(":");
		if(str.length < 5)
		{
			player.sendPacket(new CustomMessage("bbs.service.changeRace.error"));
			useSaveCommand(player);
			return;
		}

		if(player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.kamael)
		{
			player.sendPacket(new CustomMessage("bbs.service.changeRace.noChangeRaceKamael"));
			useSaveCommand(player);
			return;
		}
		if(player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.orc)
		{
			player.sendPacket(new CustomMessage("bbs.service.changeRace.noChangeRaceOrk"));
			useSaveCommand(player);
			return;
		}
		if(player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.human)
		{
			player.sendPacket(new CustomMessage("bbs.service.changeRace.noChangeRaceHuman"));
			useSaveCommand(player);
			return;
		}
		if(player.getEvent(SiegeEvent.class) != null)
		{
			player.sendPacket(new CustomMessage("bbs.service.changeRace.isEventSiege"));
			useSaveCommand(player);
			return;
		}
		if(player.isHero())
		{
			player.sendPacket(new CustomMessage("bbs.service.changeRace.isHero"));
			useSaveCommand(player);
			return;
		}
		if(!player.isInPeaceZone())
		{
			player.sendPacket(new CustomMessage("bbs.service.changeRace.noIsInPeaceZone"));
			useSaveCommand(player);
			return;
		}
		final Optional<PlayerRace> playerRace = PlayerRace.value(str[4]);
		if(!playerRace.isPresent())
		{
			player.sendPacket(new CustomMessage("bbs.service.changeRace.error"));
			useSaveCommand(player);
			return;
		}
		if(!getCheckAndPick(player, getService().getItemId(), getService().getItemCount(), true))
		{
			useSaveCommand(player);
			return;
		}
		reply(player, playerRace.get());
	}

	@Override
	public void reply(final Player player, final Object... params)
	{
		final PlayerRace playerRace = (PlayerRace) params[0];
		final PlayerRace oldRace = player.getPlayerTemplateComponent().getPlayerRace();
		player.getAppearanceComponent().setHairColor(0);
		player.getAppearanceComponent().setHairStyle(0);
		player.getAppearanceComponent().setFace(0);
		player.getPlayerTemplateComponent().setPlayerRace(playerRace);
		player.logout();
		serviceDAO.changeRace(player, player.getPlayerTemplateComponent().getPlayerRace());
		Log.service("ChangeRaceService", player, "changed race " + oldRace.name() + " to " + player.getPlayerTemplateComponent().getPlayerRace().name() + ".");
	}

}
